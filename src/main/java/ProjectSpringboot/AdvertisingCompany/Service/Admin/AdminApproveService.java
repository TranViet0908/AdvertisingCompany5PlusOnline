package ProjectSpringboot.AdvertisingCompany.Service.Admin;

import ProjectSpringboot.AdvertisingCompany.Entity.Approve;
import ProjectSpringboot.AdvertisingCompany.Repository.AdminApproveRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class AdminApproveService {

    private final AdminApproveRepository repo;

    @PersistenceContext
    private EntityManager em;

    public AdminApproveService(AdminApproveRepository repo) {
        this.repo = repo;
    }

    // ================== LIST (đổ cho templates/admin/approvals/list.html) ==================
    // Trả về các field: id, code, title, approveType, priority, status, submittedAt, dueDate,
    // amountRequested, currency, previewUrl, imageCount, videoCount, textCount, attachCount
    @Transactional(readOnly = true)
    public List<Map<String, Object>> list(String q, String status, String approveType) {
        List<Approve> headers = repo.findAllHeadersOrderByTimeDesc();
        List<Map<String, Object>> items = new ArrayList<>();

        for (Approve h : headers) {
            Map<String, Object> it = new LinkedHashMap<>();
            it.put("id",              h.getId());
            it.put("code",            h.getCode());
            it.put("title",           h.getTitle());
            it.put("approveType",     h.getApprove_type());   // enum
            it.put("priority",        h.getPriority());       // enum
            it.put("status",          h.getStatus());         // enum
            it.put("submittedAt",     h.getSubmitted_at());
            it.put("dueDate",         h.getDue_date());
            it.put("amountRequested", h.getAmount_requested());
            it.put("currency",        h.getCurrency());

            // Đếm media theo loại + previewUrl đầu tiên
            int imageCount = 0, videoCount = 0, textCount = 0;
            for (Object[] row : repo.countMediaByType(h.getId())) {
                String mt = String.valueOf(row[0]);
                long c = ((Number) row[1]).longValue();
                if ("IMAGE".equalsIgnoreCase(mt)) imageCount = (int) c;
                else if ("VIDEO".equalsIgnoreCase(mt)) videoCount = (int) c;
                else if ("TEXT".equalsIgnoreCase(mt))  textCount  = (int) c;
            }
            long attachCount = repo.countAttach(h.getId());
            it.put("imageCount",  imageCount);
            it.put("videoCount",  videoCount);
            it.put("textCount",   textCount);
            it.put("attachCount", (int) attachCount);
            it.put("previewUrl",  repo.firstMediaUrl(h.getId()).orElse(null));

            items.add(it);
        }

        // Lọc theo q/status/approveType
        if (q != null && !q.isBlank()) {
            String s = q.toLowerCase();
            items = items.stream().filter(it ->
                    optStr(it.get("code")).toLowerCase().contains(s)
                            || optStr(it.get("title")).toLowerCase().contains(s)
            ).toList();
        }
        if (status != null && !status.isBlank()) {
            items = items.stream().filter(it -> Objects.toString(it.get("status"), "").equals(status)).toList();
        }
        if (approveType != null && !approveType.isBlank()) {
            items = items.stream().filter(it -> Objects.toString(it.get("approveType"), "").equals(approveType)).toList();
        }
        return items;
    }

    // ================== CREATE (HEADER) ==================
    public Long create(Map<String, String> form) {
        Approve h = new Approve();
        h.setRow_kind(Approve.RowKind.HEADER);
        h.setCode(nullIfBlank(form.get("code")));
        h.setTitle(req(form, "title"));
        h.setDescription(nullIfBlank(form.get("description")));

        h.setApprove_type(Approve.ApproveType.valueOf(req(form, "approveType")));
        h.setPriority(Approve.Priority.valueOf(req(form, "priority")));
        h.setRequester_type(Approve.RequesterType.valueOf(req(form, "requesterType")));

        // ManyToOne qua EntityManager (không cần repo phụ)
        setRefEmployee(h::setRequester_employee, toLong(form.get("requesterEmployeeId")));
        setRefClient(h::setRequester_client,     toLong(form.get("requesterClientId")));
        setRefClient(h::setClient,               toLong(form.get("clientId")));
        setRefProject(h::setProject,             toLong(form.get("projectId")));
        setRefContract(h::setContract,           toLong(form.get("contractId")));

        h.setAmount_requested(toBigDecimal(form.get("amountRequested")));
        h.setCurrency(nullIfBlank(form.get("currency")));

        LocalDate due = parseLocalDate(form.get("dueDate"));
        h.setDue_date(due != null ? due.atStartOfDay() : null);

        // Mặc định khi admin tạo mới: SUBMITTED
        h.setStatus(Approve.ApproveStatus.SUBMITTED);
        h.setSubmitted_at(LocalDateTime.now());
        h.setCreated_at(LocalDateTime.now());

        repo.save(h);
        return h.getId();
    }

    // ================== LOAD HEADER (cho edit) ==================
    @Transactional(readOnly = true)
    public Map<String, Object> loadHeaderMap(Long id) {
        Approve h = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phê duyệt #" + id));
        if (h.getRow_kind() != Approve.RowKind.HEADER)
            throw new IllegalArgumentException("Bản ghi không phải HEADER");

        Map<String, Object> m = new LinkedHashMap<>();
        // snake_case đúng với template edit.html
        m.put("id", h.getId());
        m.put("code", h.getCode());
        m.put("title", h.getTitle());
        m.put("description", h.getDescription());

        m.put("approve_type",  h.getApprove_type());
        m.put("priority",      h.getPriority());
        m.put("status",        h.getStatus());
        m.put("requester_type",h.getRequester_type());

        m.put("requester_employee_id", h.getRequester_employee() != null ? h.getRequester_employee().getId() : null);
        m.put("requester_client_id",   h.getRequester_client()   != null ? h.getRequester_client().getId()   : null);
        m.put("client_id",             h.getClient()             != null ? h.getClient().getId()             : null);
        m.put("project_id",            h.getProject()            != null ? h.getProject().getId()            : null);
        m.put("contract_id",           h.getContract()           != null ? h.getContract().getId()           : null);

        m.put("amount_requested", h.getAmount_requested());
        m.put("currency",         h.getCurrency());
        m.put("due_date",         h.getDue_date());

        m.put("submitted_at", h.getSubmitted_at());
        m.put("decided_at",   h.getDecided_at());
        return m;
    }

    // ================== LOAD CHILDREN GROUPED (phục vụ panel media/attach ở edit) ==================
    @Transactional(readOnly = true)
    public Map<String, List<Map<String, Object>>> loadChildrenGrouped(Long headerId) {
        List<Approve> children = repo.findChildrenByRoot(headerId);

        List<Map<String, Object>> images = new ArrayList<>();
        List<Map<String, Object>> videos = new ArrayList<>();
        List<Map<String, Object>> texts  = new ArrayList<>();
        List<Map<String, Object>> attaches = new ArrayList<>();
        List<Map<String, Object>> all = new ArrayList<>();

        for (Approve c : children) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("row_kind",   c.getRow_kind());
            m.put("seq_no",     c.getSeq_no());
            m.put("created_at", c.getCreated_at());

            // MEDIA fields
            m.put("media_type",      c.getMedia_type());
            m.put("media_title",     c.getMedia_title());
            m.put("media_url",       c.getMedia_url());
            m.put("media_thumb_url", c.getMedia_thumb_url());
            m.put("media_text",      c.getMedia_text());

            // ATTACH fields
            m.put("attach_name", c.getAttach_name());
            m.put("attach_url",  c.getAttach_url());
            m.put("attach_mime", c.getAttach_mime());
            m.put("attach_size", c.getAttach_size());

            all.add(m);

            if (c.getRow_kind() == Approve.RowKind.MEDIA) {
                if (c.getMedia_type() == Approve.MediaType.IMAGE) images.add(m);
                else if (c.getMedia_type() == Approve.MediaType.VIDEO) videos.add(m);
                else if (c.getMedia_type() == Approve.MediaType.TEXT)  texts.add(m);
            } else if (c.getRow_kind() == Approve.RowKind.ATTACH) {
                attaches.add(m);
            }
        }

        Map<String, List<Map<String, Object>>> out = new LinkedHashMap<>();
        out.put("images", images);
        out.put("videos", videos);
        out.put("texts",  texts);
        out.put("attaches", attaches);
        out.put("allChildren", all);
        return out;
    }

    // ================== UPDATE (HEADER) ==================
    public void update(Long id, Map<String, String> form) {
        Approve h = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy #" + id));
        if (h.getRow_kind() != Approve.RowKind.HEADER)
            throw new IllegalArgumentException("Bản ghi không phải HEADER");

        h.setCode(nullIfBlank(form.get("code")));
        h.setTitle(req(form, "title"));
        h.setDescription(nullIfBlank(form.get("description")));

        if (notBlank(form.get("approveType")))
            h.setApprove_type(Approve.ApproveType.valueOf(form.get("approveType")));
        if (notBlank(form.get("priority")))
            h.setPriority(Approve.Priority.valueOf(form.get("priority")));
        if (notBlank(form.get("status")))
            h.setStatus(Approve.ApproveStatus.valueOf(form.get("status")));
        if (notBlank(form.get("requesterType")))
            h.setRequester_type(Approve.RequesterType.valueOf(form.get("requesterType")));

        setRefEmployee(h::setRequester_employee, toLong(form.get("requesterEmployeeId")));
        setRefClient(h::setRequester_client,     toLong(form.get("requesterClientId")));
        setRefClient(h::setClient,               toLong(form.get("clientId")));
        setRefProject(h::setProject,             toLong(form.get("projectId")));
        setRefContract(h::setContract,           toLong(form.get("contractId")));

        h.setAmount_requested(toBigDecimal(form.get("amountRequested")));
        h.setCurrency(nullIfBlank(form.get("currency")));
        LocalDate due = parseLocalDate(form.get("dueDate"));
        h.setDue_date(due != null ? due.atStartOfDay() : null);

        h.setUpdated_at(LocalDateTime.now());
        // @Transactional sẽ tự flush
    }

    // ================== DELETE (HEADER + toàn bộ child) ==================
    public void deleteCascade(Long id) {
        Approve h = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy #" + id));
        if (h.getRow_kind() != Approve.RowKind.HEADER)
            throw new IllegalArgumentException("Bản ghi không phải HEADER");
        var children = repo.findChildrenByRoot(id);
        repo.deleteAll(children);
        repo.delete(h);
    }

    // ================== MEDIA / COMMENT / ATTACH ==================
    @Transactional
    public void addMedia(Long headerId, Map<String, String> form) {
        Approve header = repo.findById(headerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy #" + headerId));
        if (header.getRow_kind() != Approve.RowKind.HEADER)
            throw new IllegalArgumentException("Bản ghi không phải HEADER");

        Approve.MediaType mt = Approve.MediaType.valueOf(
                Objects.toString(form.get("media_type"), "IMAGE")
        );

        Approve c = new Approve();
        c.setRoot(header); // << quan trọng
        c.setRow_kind(Approve.RowKind.MEDIA);
        c.setSeq_no(Optional.ofNullable(repo.findMaxSeqNo(headerId)).orElse(0) + 1);
        c.setMedia_type(mt);
        c.setMedia_title(nullIfBlank(form.get("media_title")));
        c.setMedia_url(nullIfBlank(form.get("media_url")));
        c.setMedia_thumb_url(nullIfBlank(form.get("media_thumb_url")));
        c.setCreated_at(LocalDateTime.now());
        repo.save(c);
    }

    @Transactional
    public void addComment(Long headerId, Map<String, String> form) {
        Approve header = repo.findById(headerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy #" + headerId));
        if (header.getRow_kind() != Approve.RowKind.HEADER)
            throw new IllegalArgumentException("Bản ghi không phải HEADER");

        Approve c = new Approve();
        c.setRoot(header); // <<
        c.setRow_kind(Approve.RowKind.MEDIA);
        c.setSeq_no(Optional.ofNullable(repo.findMaxSeqNo(headerId)).orElse(0) + 1);
        c.setMedia_type(Approve.MediaType.TEXT);
        c.setMedia_title(nullIfBlank(form.get("media_title")));
        c.setMedia_text(Objects.requireNonNullElse(form.get("media_text"), "").trim());
        c.setCreated_at(LocalDateTime.now());
        repo.save(c);
    }

    @Transactional
    public void addAttach(Long headerId, Map<String, String> form) {
        Approve header = repo.findById(headerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy #" + headerId));
        if (header.getRow_kind() != Approve.RowKind.HEADER)
            throw new IllegalArgumentException("Bản ghi không phải HEADER");

        Approve a = new Approve();
        a.setRoot(header); // <<
        a.setRow_kind(Approve.RowKind.ATTACH);
        a.setSeq_no(Optional.ofNullable(repo.findMaxSeqNo(headerId)).orElse(0) + 1);
        a.setAttach_name(nullIfBlank(form.get("attach_name")));
        a.setAttach_url(Objects.requireNonNullElse(form.get("attach_url"), "").trim());
        a.setAttach_mime(nullIfBlank(form.get("attach_mime")));
        a.setCreated_at(LocalDateTime.now());
        repo.save(a);
    }

    @Transactional
    public void deleteChild(Long headerId, Long childId) {
        Approve child = repo.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy child #" + childId));
        Long childRootId = (child.getRoot() != null ? child.getRoot().getId() : null);
        if (!Objects.equals(childRootId, headerId))
            throw new IllegalArgumentException("Child không thuộc header #" + headerId);
        if (child.getRow_kind() == Approve.RowKind.HEADER)
            throw new IllegalArgumentException("Không thể xóa HEADER bằng API này");
        repo.delete(child);
    }

    // ================== Danh mục cho form (lấy dữ liệu thật) ==================
    @Transactional(readOnly = true)
    public void bindFormLists(Model model) {
        // enums + currency như cũ
        model.addAttribute("allApproveTypes", Arrays.asList(Approve.ApproveType.values()));
        model.addAttribute("allPriorities", Arrays.asList(Approve.Priority.values()));
        model.addAttribute("allRequesterTypes", Arrays.asList(Approve.RequesterType.values()));
        model.addAttribute("allStatuses", Arrays.asList(Approve.ApproveStatus.values()));
        model.addAttribute("currencies", List.of("VND", "USD", "EUR"));

        // Lấy Entity thật từ DB
        var employees = em.createQuery(
                "select e from ProjectSpringboot.AdvertisingCompany.Entity.Employee e order by e.id asc",
                ProjectSpringboot.AdvertisingCompany.Entity.Employee.class
        ).getResultList();

        var clients = em.createQuery(
                "select c from ProjectSpringboot.AdvertisingCompany.Entity.Client c order by c.id asc",
                ProjectSpringboot.AdvertisingCompany.Entity.Client.class
        ).getResultList();

        var projects = em.createQuery(
                "select p from ProjectSpringboot.AdvertisingCompany.Entity.AdProject p order by p.id asc",
                ProjectSpringboot.AdvertisingCompany.Entity.AdProject.class
        ).getResultList();

        var contracts = em.createQuery(
                "select ct from ProjectSpringboot.AdvertisingCompany.Entity.Contract ct order by ct.id asc",
                ProjectSpringboot.AdvertisingCompany.Entity.Contract.class
        ).getResultList();

        // Chuẩn hóa về List<Map> để khớp VỚI template hiện tại:
        //   employees: id, fullName
        //   clients:   id, name
        //   projects:  id, name
        //   contracts: id, code
        List<Map<String,Object>> employeesVm = new ArrayList<>();
        for (var e : employees) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", invokeOrNull(e, "getId"));
            // ưu tiên getFullName -> getName -> getCode -> "ID <id>"
            String fullName = firstNonNull(
                    str(invokeOrNull(e, "getFullName")),
                    str(invokeOrNull(e, "getName")),
                    str(invokeOrNull(e, "getCode")),
                    "ID " + invokeOrNull(e, "getId")
            );
            m.put("fullName", fullName);
            employeesVm.add(m);
        }

        List<Map<String,Object>> clientsVm = new ArrayList<>();
        for (var c : clients) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", invokeOrNull(c, "getId"));
            String name = firstNonNull(
                    str(invokeOrNull(c, "getName")),
                    str(invokeOrNull(c, "getCompanyName")),
                    str(invokeOrNull(c, "getFullName")),
                    "ID " + invokeOrNull(c, "getId")
            );
            m.put("name", name);
            clientsVm.add(m);
        }

        List<Map<String,Object>> projectsVm = new ArrayList<>();
        for (var p : projects) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", invokeOrNull(p, "getId"));
            String name = firstNonNull(
                    str(invokeOrNull(p, "getName")),
                    str(invokeOrNull(p, "getTitle")),
                    str(invokeOrNull(p, "getCode")),
                    "ID " + invokeOrNull(p, "getId")
            );
            m.put("name", name);
            projectsVm.add(m);
        }

        List<Map<String,Object>> contractsVm = new ArrayList<>();
        for (var ct : contracts) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", invokeOrNull(ct, "getId"));
            String code = firstNonNull(
                    str(invokeOrNull(ct, "getCode")),
                    str(invokeOrNull(ct, "getContractCode")),
                    str(invokeOrNull(ct, "getNumber")),
                    "ID " + invokeOrNull(ct, "getId")
            );
            m.put("code", code);
            contractsVm.add(m);
        }

        model.addAttribute("employees", employeesVm);
        model.addAttribute("clients", clientsVm);
        model.addAttribute("adProjects", projectsVm);
        model.addAttribute("contracts", contractsVm);
    }

    // ================== Helpers ==================
    private static String req(Map<String, String> f, String k) {
        String v = f.get(k);
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Thiếu trường bắt buộc: " + k);
        return v.trim();
    }
    private static String nullIfBlank(String s){ return (s==null||s.isBlank())?null:s.trim(); }
    private static boolean notBlank(String s){ return s!=null && !s.isBlank(); }
    private static BigDecimal toBigDecimal(String s){
        try { return (s==null||s.isBlank()) ? null : new BigDecimal(s); }
        catch (Exception e){ return null; }
    }
    private static Long toLong(String s){
        try { return (s==null||s.isBlank()) ? null : Long.parseLong(s); }
        catch (Exception e){ return null; }
    }
    private static LocalDate parseLocalDate(String s){
        try { return (s==null||s.isBlank()) ? null : LocalDate.parse(s); }
        catch (Exception e){ return null; }
    }
    private static String optStr(Object o){ return o==null ? "" : String.valueOf(o); }

    // Set reference helpers (tránh tạo thêm repository)
    private void setRefEmployee(java.util.function.Consumer<ProjectSpringboot.AdvertisingCompany.Entity.Employee> setter, Long id){
        if (id == null) { setter.accept(null); return; }
        var ref = em.getReference(ProjectSpringboot.AdvertisingCompany.Entity.Employee.class, id);
        setter.accept(ref);
    }
    private void setRefClient(java.util.function.Consumer<ProjectSpringboot.AdvertisingCompany.Entity.Client> setter, Long id){
        if (id == null) { setter.accept(null); return; }
        var ref = em.getReference(ProjectSpringboot.AdvertisingCompany.Entity.Client.class, id);
        setter.accept(ref);
    }
    private void setRefProject(java.util.function.Consumer<ProjectSpringboot.AdvertisingCompany.Entity.AdProject> setter, Long id){
        if (id == null) { setter.accept(null); return; }
        var ref = em.getReference(ProjectSpringboot.AdvertisingCompany.Entity.AdProject.class, id);
        setter.accept(ref);
    }
    private void setRefContract(java.util.function.Consumer<ProjectSpringboot.AdvertisingCompany.Entity.Contract> setter, Long id){
        if (id == null) { setter.accept(null); return; }
        var ref = em.getReference(ProjectSpringboot.AdvertisingCompany.Entity.Contract.class, id);
        setter.accept(ref);
    }
    private static Object invokeOrNull(Object target, String method) {
        try { return target.getClass().getMethod(method).invoke(target); }
        catch (Exception ignored) { return null; }
    }
    private static String str(Object o) { return o == null ? null : String.valueOf(o); }
    private static String firstNonNull(String... xs) {
        for (String s : xs) if (s != null && !s.isBlank()) return s;
        return null;
    }

}
