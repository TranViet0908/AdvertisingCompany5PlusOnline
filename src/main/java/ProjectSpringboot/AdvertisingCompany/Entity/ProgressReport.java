package ProjectSpringboot.AdvertisingCompany.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress_report")
public class ProgressReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private AdProject project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    public ProgressReport(Long id, AdProject project, Employee employee, LocalDate reportDate, String content, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.project = project;
        this.employee = employee;
        this.reportDate = reportDate;
        this.content = content;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public ProgressReport(AdProject project, Employee employee, LocalDate reportDate, String content, LocalDateTime created_at, LocalDateTime updated_at) {
        this.project = project;
        this.employee = employee;
        this.reportDate = reportDate;
        this.content = content;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public ProgressReport() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AdProject getProject() {
        return project;
    }

    public void setProject(AdProject project) {
        this.project = project;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
