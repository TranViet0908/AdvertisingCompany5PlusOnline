package ProjectSpringboot.AdvertisingCompany.Service;

import ProjectSpringboot.AdvertisingCompany.Entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service tổng hợp cho module giao diện /user/contracts/*
 * Không thêm repository mới. Dùng EntityManager + JPQL để khớp schema hiện tại.
 * Ánh xạ dữ liệu ra các DTO gọn cho view.
 */
@Service
public class UserContractService {

    @PersistenceContext
    private EntityManager em;

    // ===== DTOs dùng cho view =====
    public record ContractView(
            Long id,
            String code,           // "CT-" + id
            String status,         // "paid" | "active" | text khác
            LocalDate signedAt,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal totalValue,
            BigDecimal paidAmount
    ) {}

    public record PaymentRow(
            Long id,
            Long contractId,
            Long projectId,
            String name,
            LocalDate signDate,
            BigDecimal totalCost,
            BigDecimal amountPaid,
            String status,
            BigDecimal outstanding
    ) {}

    public record PaymentSummary(
            BigDecimal total,
            BigDecimal paid,
            BigDecimal outstanding
    ) {}

    public record ApprovalItem(
            Long id,
            String title,
            LocalDate reportDate,
            String previewUrl,
            String contractCode
    ) {}

    public record ProgressStep(
            String title,
            LocalDate date,
            String owner,
            String status,
            int progress,
            String note,
            String attachmentUrl
    ) {}

    // ===== Public APIs =====

    /** Hợp đồng của client theo username đang đăng nhập (ROLE_CLIENT). */
    public List<ContractView> contractsOfUser(String username) {
        String email = resolveEmailByUsername(username);

        // Lấy tất cả hợp đồng gắn với client có email này
        TypedQuery<Contract> q = em.createQuery("""
                SELECT c FROM Contract c
                JOIN c.client cl
                JOIN c.project pr
                WHERE cl.email = :email
                ORDER BY c.id DESC
                """, Contract.class);
        q.setParameter("email", email);
        List<Contract> contracts = q.getResultList();

        // Map contractId -> tổng amountPaid từ Payment của client này
        Map<Long, BigDecimal> paidByContract = sumPaidByContract(email);

        List<ContractView> result = new ArrayList<>();
        for (Contract c : contracts) {
            Long id = c.getId();
            AdProject pr = c.getProject();
            BigDecimal paid = paidByContract.getOrDefault(id, BigDecimal.ZERO);
            BigDecimal total = toBig(c.getValue());
            String status = paid.compareTo(total) >= 0 ? "paid" : "active";

            result.add(new ContractView(
                    id,
                    "CT-" + id,
                    status,
                    c.getSignDate(),
                    pr != null ? pr.getStart_date() : null,
                    pr != null ? pr.getEnd_date() : null,
                    total,
                    paid
            ));
        }
        return result;
    }

    /** Danh sách payment theo client (optional lọc contractId) */
    public List<PaymentRow> paymentsOfUser(String username, Optional<Long> contractId) {
        String email = resolveEmailByUsername(username);

        String jpql = "SELECT p FROM Payment p JOIN p.client cl WHERE cl.email = :email";
        if (contractId.isPresent()) jpql += " AND p.contract.id = :cid";
        jpql += " ORDER BY p.id DESC";

        TypedQuery<Payment> q = em.createQuery(jpql, Payment.class)
                .setParameter("email", email);
        contractId.ifPresent(cid -> q.setParameter("cid", cid));

        List<Payment> list = q.getResultList();
        return list.stream().map(this::toRow).collect(Collectors.toList());
    }

    /** Tổng hợp thanh toán của client (optional theo contractId) */
    public PaymentSummary paymentSummaryOfUser(String username, Optional<Long> contractId) {
        List<PaymentRow> rows = paymentsOfUser(username, contractId);
        BigDecimal total = rows.stream().map(PaymentRow::totalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paid  = rows.stream().map(PaymentRow::amountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PaymentSummary(total, paid, total.subtract(paid));
    }

    /** Thanh toán thêm vào amount_paid cho 1 bản ghi Payment. */
    @Transactional
    public void payMore(String username, Long paymentId, BigDecimal payAmount) {
        if (payAmount == null || payAmount.signum() <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán không hợp lệ.");
        }

        String email = resolveEmailByUsername(username);

        Payment p = em.find(Payment.class, paymentId);
        if (p == null) throw new IllegalArgumentException("Payment không tồn tại.");

        // bảo vệ quyền: payment phải thuộc cùng client email
        if (p.getClient() == null || p.getClient().getEmail() == null || !p.getClient().getEmail().equals(email)) {
            throw new IllegalStateException("Bạn không có quyền thanh toán bản ghi này.");
        }

        BigDecimal total = toBig(p.getTotalCost());
        BigDecimal paid  = toBig(p.getAmountPaid());
        BigDecimal after = paid.add(payAmount);

        if (after.compareTo(total) > 0) {
            throw new IllegalStateException("Số tiền vượt quá tổng chi phí còn lại.");
        }

        p.setAmountPaid(after.doubleValue());

        // Nếu đủ thì set trạng thái DONE; nếu còn nợ, set STILL_IN_DEBT
        try {
            if (after.compareTo(total) == 0) {
                p.setStatus(Payment.Status.DONE);
            } else if (after.signum() >= 0) {
                p.setStatus(Payment.Status.STILL_IN_DEBT);
            }
        } catch (Exception ignore) {}

        em.merge(p);
    }

    /** Items cần phê duyệt (đọc từ ProgressReport, chưa có cột decision nên chỉ hiển thị). */
    public List<ApprovalItem> approvalsOfUser(String username) {
        String email = resolveEmailByUsername(username);
        TypedQuery<ProgressReport> q = em.createQuery("""
                SELECT pr FROM ProgressReport pr
                JOIN pr.project prj
                JOIN prj.client cl
                WHERE cl.email = :email
                ORDER BY pr.reportDate DESC
                """, ProgressReport.class);
        q.setParameter("email", email);
        List<ProgressReport> list = q.getResultList();

        return list.stream().map(pr ->
                new ApprovalItem(
                        pr.getId(),
                        "Báo cáo dự án: " + (pr.getProject() != null ? pr.getProject().getName() : ("PR-" + pr.getId())),
                        pr.getReportDate(),
                        null,
                        pr.getProject() != null ? ("CT-" + pr.getProject().getId()) : "N/A"
                )
        ).collect(Collectors.toList());
    }

    /** Timeline tiến trình: lấy từ WorkTask theo dự án của client. */
    public List<ProgressStep> progressOfUser(String username, Optional<Long> contractId) {
        String email = resolveEmailByUsername(username);

        String jpql = """
                SELECT wt FROM WorkTask wt
                JOIN wt.adProject prj
                JOIN prj.client cl
                WHERE cl.email = :email
                """;
        if (contractId.isPresent()) {
            jpql += " AND prj.id IN (SELECT c.project.id FROM Contract c WHERE c.id = :cid)";
        }
        jpql += " ORDER BY wt.start_date ASC";

        TypedQuery<WorkTask> q = em.createQuery(jpql, WorkTask.class)
                .setParameter("email", email);
        contractId.ifPresent(cid -> q.setParameter("cid", cid));

        return q.getResultList().stream().map(this::toStep).collect(Collectors.toList());
    }

    // ===== Helpers =====

    private String resolveEmailByUsername(String username) {
        // Không đụng UserRepository; truy vấn trực tiếp
        TypedQuery<User> uq = em.createQuery("SELECT u FROM User u WHERE u.username = :u", User.class);
        uq.setParameter("u", username);
        User user = uq.getResultStream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy user: " + username));

        if (user.getRole() == null || !user.getRole().contains("ROLE_CLIENT")) {
            throw new IllegalStateException("Chỉ CLIENT mới dùng được khu vực này.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalStateException("Tài khoản chưa có email để đối chiếu với Client.");
        }
        return user.getEmail();
    }

    private Map<Long, BigDecimal> sumPaidByContract(String email) {
        // contractId -> sum(amountPaid)
        TypedQuery<Object[]> sq = em.createQuery("""
                SELECT p.contract.id, COALESCE(SUM(p.amountPaid),0)
                FROM Payment p
                JOIN p.client cl
                WHERE cl.email = :email
                GROUP BY p.contract.id
                """, Object[].class);
        sq.setParameter("email", email);
        Map<Long, BigDecimal> map = new HashMap<>();
        for (Object[] row : sq.getResultList()) {
            Long cid = (Long) row[0];
            Double sum = (Double) row[1];
            map.put(cid, toBig(sum));
        }
        return map;
    }

    private PaymentRow toRow(Payment p) {
        BigDecimal total = toBig(p.getTotalCost());
        BigDecimal paid  = toBig(p.getAmountPaid());
        BigDecimal rest  = total.subtract(paid);
        String statusStr = String.valueOf(p.getStatus());
        return new PaymentRow(
                p.getId(),
                p.getContract() != null ? p.getContract().getId() : null,
                p.getProject()  != null ? p.getProject().getId()  : null,
                p.getName(),
                p.getSignDate(),
                total,
                paid,
                statusStr,
                rest.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : rest
        );
    }

    private ProgressStep toStep(WorkTask wt) {
        String status = wt.getStatus();
        int progress = switch (status == null ? "" : status.toUpperCase()) {
            case "DONE", "COMPLETED" -> 100;
            case "IN_PROGRESS", "PROCESSING" -> 60;
            case "PENDING", "TODO" -> 10;
            default -> 0;
        };
        return new ProgressStep(
                wt.getName(),
                wt.getStart_date(),
                wt.getEmployee() != null ? wt.getEmployee().getFullName() : "N/A",
                status != null ? status.toLowerCase() : "pending",
                progress,
                wt.getDescription(),
                null
        );
    }

    private static BigDecimal toBig(Double d) {
        return d == null ? BigDecimal.ZERO : BigDecimal.valueOf(d);
    }
}
