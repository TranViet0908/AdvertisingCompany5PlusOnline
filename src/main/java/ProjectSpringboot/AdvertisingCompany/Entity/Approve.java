package ProjectSpringboot.AdvertisingCompany.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "approve")
public class Approve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // self reference: header -> children
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id")
    private Approve root;

    @OneToMany(mappedBy = "root", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("seq_no ASC, id ASC")
    private List<Approve> children;

    @Enumerated(EnumType.STRING)
    @Column(name = "row_kind", nullable = false)
    private RowKind row_kind;

    @Column(name = "seq_no")
    private Integer seq_no;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "approve_type")
    private ApproveType approve_type;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private ApproveStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "requester_type")
    private RequesterType requester_type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_employee_id")
    private Employee requester_employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_client_id")
    private Client requester_client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_employee_id")
    private Employee approver_employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private AdProject project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @Column(name = "due_date")
    private LocalDateTime due_date;

    @Column(name = "submitted_at")
    private LocalDateTime submitted_at;

    @Column(name = "decided_at")
    private LocalDateTime decided_at;

    @Column(name = "amount_requested", precision = 18, scale = 2)
    private BigDecimal amount_requested;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "amount_approved", precision = 18, scale = 2)
    private BigDecimal amount_approved;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type")
    private MediaType media_type;

    @Column(name = "media_title", length = 255)
    private String media_title;

    @Column(name = "media_url", length = 500)
    private String media_url;

    @Column(name = "media_thumb_url", length = 500)
    private String media_thumb_url;

    @Column(name = "media_text", columnDefinition = "mediumtext")
    private String media_text;

    @Column(name = "attach_name", length = 255)
    private String attach_name;

    @Column(name = "attach_url", length = 500)
    private String attach_url;

    @Column(name = "attach_mime", length = 100)
    private String attach_mime;

    @Column(name = "attach_size")
    private Long attach_size;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    // ===== Enums inside Approve =====
    public enum RowKind { HEADER, MEDIA, ATTACH }
    public enum ApproveType { PRODUCT, COST, PROJECT_PLAN, CONTRACT_CHANGE, ASSET, OTHER }
    public enum Priority { LOW, MEDIUM, HIGH, URGENT }
    public enum ApproveStatus { DRAFT, SUBMITTED, UNDER_REVIEW, NEEDS_REVISION, APPROVED, REJECTED, CANCELLED }
    public enum RequesterType { EMPLOYEE, CLIENT }
    public enum MediaType { IMAGE, VIDEO, DOC, TEXT }

    // ===== Getters/Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Approve getRoot() { return root; }
    public void setRoot(Approve root) { this.root = root; }
    public List<Approve> getChildren() { return children; }
    public void setChildren(List<Approve> children) { this.children = children; }
    public RowKind getRow_kind() { return row_kind; }
    public void setRow_kind(RowKind row_kind) { this.row_kind = row_kind; }
    public Integer getSeq_no() { return seq_no; }
    public void setSeq_no(Integer seq_no) { this.seq_no = seq_no; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ApproveType getApprove_type() { return approve_type; }
    public void setApprove_type(ApproveType approve_type) { this.approve_type = approve_type; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public ApproveStatus getStatus() { return status; }
    public void setStatus(ApproveStatus status) { this.status = status; }
    public RequesterType getRequester_type() { return requester_type; }
    public void setRequester_type(RequesterType requester_type) { this.requester_type = requester_type; }
    public Employee getRequester_employee() { return requester_employee; }
    public void setRequester_employee(Employee requester_employee) { this.requester_employee = requester_employee; }
    public Client getRequester_client() { return requester_client; }
    public void setRequester_client(Client requester_client) { this.requester_client = requester_client; }
    public Employee getApprover_employee() { return approver_employee; }
    public void setApprover_employee(Employee approver_employee) { this.approver_employee = approver_employee; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public AdProject getProject() { return project; }
    public void setProject(AdProject project) { this.project = project; }
    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }
    public LocalDateTime getDue_date() { return due_date; }
    public void setDue_date(LocalDateTime due_date) { this.due_date = due_date; }
    public LocalDateTime getSubmitted_at() { return submitted_at; }
    public void setSubmitted_at(LocalDateTime submitted_at) { this.submitted_at = submitted_at; }
    public LocalDateTime getDecided_at() { return decided_at; }
    public void setDecided_at(LocalDateTime decided_at) { this.decided_at = decided_at; }
    public BigDecimal getAmount_requested() { return amount_requested; }
    public void setAmount_requested(BigDecimal amount_requested) { this.amount_requested = amount_requested; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getAmount_approved() { return amount_approved; }
    public void setAmount_approved(BigDecimal amount_approved) { this.amount_approved = amount_approved; }
    public MediaType getMedia_type() { return media_type; }
    public void setMedia_type(MediaType media_type) { this.media_type = media_type; }
    public String getMedia_title() { return media_title; }
    public void setMedia_title(String media_title) { this.media_title = media_title; }
    public String getMedia_url() { return media_url; }
    public void setMedia_url(String media_url) { this.media_url = media_url; }
    public String getMedia_thumb_url() { return media_thumb_url; }
    public void setMedia_thumb_url(String media_thumb_url) { this.media_thumb_url = media_thumb_url; }
    public String getMedia_text() { return media_text; }
    public void setMedia_text(String media_text) { this.media_text = media_text; }
    public String getAttach_name() { return attach_name; }
    public void setAttach_name(String attach_name) { this.attach_name = attach_name; }
    public String getAttach_url() { return attach_url; }
    public void setAttach_url(String attach_url) { this.attach_url = attach_url; }
    public String getAttach_mime() { return attach_mime; }
    public void setAttach_mime(String attach_mime) { this.attach_mime = attach_mime; }
    public Long getAttach_size() { return attach_size; }
    public void setAttach_size(Long attach_size) { this.attach_size = attach_size; }
    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }
    public LocalDateTime getUpdated_at() { return updated_at; }
    public void setUpdated_at(LocalDateTime updated_at) { this.updated_at = updated_at; }
}
