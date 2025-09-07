package ProjectSpringboot.AdvertisingCompany.Service.User;

import ProjectSpringboot.AdvertisingCompany.Entity.Approve;
import ProjectSpringboot.AdvertisingCompany.Entity.Approve.ApproveStatus;
import ProjectSpringboot.AdvertisingCompany.Entity.Approve.MediaType;
import ProjectSpringboot.AdvertisingCompany.Entity.Approve.RowKind;
import ProjectSpringboot.AdvertisingCompany.Repository.ApproveRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ApproveService {

    private final ApproveRepository repo;

    public ApproveService(ApproveRepository repo) {
        this.repo = repo;
    }

    /** Dữ liệu cho approvals.html: List<Map> với key camelCase đúng template */
    public List<Map<String, Object>> listHeadersForListPage() {
        List<Approve> headers = repo.findAllHeadersOrderByTimeDesc();
        List<Map<String, Object>> items = new ArrayList<>();

        for (Approve h : headers) {
            Map<String, Object> it = new LinkedHashMap<>();
            it.put("id", h.getId());
            it.put("code", h.getCode());
            it.put("title", h.getTitle());
            it.put("status", h.getStatus()); // enum
            it.put("approveType", h.getApprove_type()); // enum
            it.put("priority", h.getPriority()); // enum
            it.put("submittedAt", h.getSubmitted_at());
            it.put("dueDate", h.getDue_date());
            it.put("amountRequested", h.getAmount_requested());
            it.put("currency", h.getCurrency());

            // media counts
            int imageCount = 0, videoCount = 0;
            for (Object[] row : repo.countMediaByType(h.getId())) {
                String type = String.valueOf(row[0]);
                long c = ((Number) row[1]).longValue();
                if ("IMAGE".equalsIgnoreCase(type)) imageCount = (int) c;
                else if ("VIDEO".equalsIgnoreCase(type)) videoCount = (int) c;
            }
            long attachCount = repo.countAttach(h.getId());
            it.put("imageCount", imageCount);
            it.put("videoCount", videoCount);
            it.put("attachCount", (int) attachCount);

            // preview
            it.put("previewUrl", repo.firstMediaUrl(h.getId()).orElse(null));

            items.add(it);
        }
        return items;
    }

    /** Dữ liệu cho details.html: header(Map snake_case), children(List<Map snake_case>), comments(List<CommentView>) */
    public Map<String, Object> loadDetails(Long headerId) {
        Approve header = repo.findById(headerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phê duyệt #" + headerId));
        if (header.getRow_kind() != RowKind.HEADER) {
            throw new IllegalArgumentException("Bản ghi #" + headerId + " không phải HEADER.");
        }

        // header (snake_case để khớp template)
        Map<String, Object> headerMap = new LinkedHashMap<>();
        headerMap.put("id", header.getId());
        headerMap.put("code", header.getCode());
        headerMap.put("title", header.getTitle());
        headerMap.put("description", header.getDescription());
        headerMap.put("approve_type", header.getApprove_type());
        headerMap.put("priority", header.getPriority());
        headerMap.put("status", header.getStatus());
        headerMap.put("submitted_at", header.getSubmitted_at());
        headerMap.put("due_date", header.getDue_date());
        headerMap.put("decided_at", header.getDecided_at());
        headerMap.put("client_id", header.getClient() != null ? header.getClient().getId() : null);
        headerMap.put("project_id", header.getProject() != null ? header.getProject().getId() : null);
        headerMap.put("contract_id", header.getContract() != null ? header.getContract().getId() : null);
        headerMap.put("amount_requested", header.getAmount_requested());
        headerMap.put("currency", header.getCurrency());
        headerMap.put("amount_approved", header.getAmount_approved());
        headerMap.put("approver_employee_id", header.getApprover_employee() != null ? header.getApprover_employee().getId() : null);
        headerMap.put("requester_employee_id", header.getRequester_employee() != null ? header.getRequester_employee().getId() : null);
        headerMap.put("requester_client_id", header.getRequester_client() != null ? header.getRequester_client().getId() : null);
        headerMap.put("requester_type", header.getRequester_type());

        // children
        List<Approve> children = repo.findChildrenByRoot(headerId);
        List<Map<String, Object>> childMaps = new ArrayList<>();
        List<CommentView> comments = new ArrayList<>();

        for (Approve c : children) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("row_kind", c.getRow_kind());          // enum
            m.put("seq_no", c.getSeq_no());
            m.put("media_type", c.getMedia_type());      // enum
            m.put("media_title", c.getMedia_title());
            m.put("media_url", c.getMedia_url());
            m.put("media_thumb_url", c.getMedia_thumb_url());
            m.put("media_text", c.getMedia_text());
            m.put("attach_name", c.getAttach_name());
            m.put("attach_url", c.getAttach_url());
            m.put("attach_mime", c.getAttach_mime());
            m.put("attach_size", c.getAttach_size());
            m.put("created_at", c.getCreated_at());

            childMaps.add(m);

            // gom bình luận (MEDIA + TEXT) cho khối "Thảo luận"
            if (c.getRow_kind() == RowKind.MEDIA && c.getMedia_type() == MediaType.TEXT) {
                comments.add(new CommentView(
                        c.getMedia_title() != null ? c.getMedia_title() : "Người dùng",
                        c.getCreated_at(),
                        c.getMedia_text()
                ));
            }
        }

        Map<String, Object> out = new HashMap<>();
        out.put("header", headerMap);
        out.put("children", childMaps);
        out.put("comments", comments);
        return out;
    }

    /** Thêm bình luận (MEDIA + TEXT) làm child của header */
    public void addComment(Long headerId, String content, String actorName) {
        Approve header = repo.findById(headerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phê duyệt #" + headerId));
        if (header.getRow_kind() != RowKind.HEADER) {
            throw new IllegalArgumentException("Bản ghi #" + headerId + " không phải HEADER.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung bình luận không được rỗng.");
        }

        Integer maxSeq = repo.findMaxSeqNo(headerId);
        Approve child = new Approve();
        child.setRoot(header);
        child.setRow_kind(RowKind.MEDIA);
        child.setSeq_no(maxSeq != null ? maxSeq + 1 : 1);
        child.setClient(header.getClient());
        child.setProject(header.getProject());
        child.setContract(header.getContract());
        child.setMedia_type(MediaType.TEXT);
        child.setMedia_title(actorName != null ? actorName : "Người dùng");
        child.setMedia_text(content.trim());
        child.setCreated_at(LocalDateTime.now());
        repo.save(child);
    }

    /** Quyết định: APPROVED | REJECTED | NEEDS_REVISION, kèm góp ý (tạo child TEXT nếu có) */
    public void submitDecision(Long headerId, String decision, String comment, String actorName) {
        Approve header = repo.findById(headerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phê duyệt #" + headerId));
        if (header.getRow_kind() != RowKind.HEADER) {
            throw new IllegalArgumentException("Bản ghi #" + headerId + " không phải HEADER.");
        }

        ApproveStatus newStatus;
        String d = decision == null ? "" : decision.trim().toUpperCase();
        switch (d) {
            case "APPROVED" -> newStatus = ApproveStatus.APPROVED;
            case "REJECTED" -> newStatus = ApproveStatus.REJECTED;
            case "NEEDS_REVISION" -> newStatus = ApproveStatus.NEEDS_REVISION;
            default -> throw new IllegalArgumentException("Giá trị decision không hợp lệ: " + decision);
        }

        header.setStatus(newStatus);
        header.setDecided_at(LocalDateTime.now());

        if (comment != null && !comment.trim().isEmpty()) {
            addComment(headerId, "[" + d + "] " + comment.trim(), actorName);
        }
        // flush do @Transactional quản lý
    }

    // ====== View model tối giản cho khối comments (đúng key template) ======
    public static class CommentView {
        private final String authorName;
        private final LocalDateTime createdAt;
        private final String content;

        public CommentView(String authorName, LocalDateTime createdAt, String content) {
            this.authorName = authorName;
            this.createdAt = createdAt;
            this.content = content;
        }

        public String getAuthorName() { return authorName; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public String getContent() { return content; }
    }
}
