package ProjectSpringboot.AdvertisingCompany.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private AdProject project;

    @Column(nullable = false)
    private Double value;

    @Column(name = "sign_date")
    private LocalDate signDate;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    public Contract(Long id, Client client, AdProject project, Double value, LocalDate signDate, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.client = client;
        this.project = project;
        this.value = value;
        this.signDate = signDate;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Contract(Client client, AdProject project, Double value, LocalDate signDate, LocalDateTime created_at, LocalDateTime updated_at) {
        this.client = client;
        this.project = project;
        this.value = value;
        this.signDate = signDate;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Contract(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public AdProject getProject() {
        return project;
    }

    public void setProject(AdProject project) {
        this.project = project;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public LocalDate getSignDate() {
        return signDate;
    }

    public void setSignDate(LocalDate signDate) {
        this.signDate = signDate;
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
