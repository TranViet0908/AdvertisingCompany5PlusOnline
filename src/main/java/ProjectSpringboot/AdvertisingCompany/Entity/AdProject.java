package ProjectSpringboot.AdvertisingCompany.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="ad_project")
public class AdProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "start_date")
    private LocalDate start_date;

    @Column(name = "end_date")
    private LocalDate end_date;

    @Column(name = "budget")
    private Double budget;

    @Column(name = "actual_cost")
    private Double actual_cost;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    public AdProject(Long id, String name, Client client, LocalDate start_date, LocalDate end_date, Double budget, Double actual_cost, String status, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.name = name;
        this.client = client;
        this.start_date = start_date;
        this.end_date = end_date;
        this.budget = budget;
        this.actual_cost = actual_cost;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public AdProject(String name, Client client, LocalDate start_date, LocalDate end_date, Double budget, Double actual_cost, String status, LocalDateTime created_at, LocalDateTime updated_at) {
        this.name = name;
        this.client = client;
        this.start_date = start_date;
        this.end_date = end_date;
        this.budget = budget;
        this.actual_cost = actual_cost;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public AdProject(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Double getActual_cost() {
        return actual_cost;
    }

    public void setActual_cost(Double actual_cost) {
        this.actual_cost = actual_cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
