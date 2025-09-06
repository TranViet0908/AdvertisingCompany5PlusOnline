package ProjectSpringboot.AdvertisingCompany.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    public enum Topic { APPROVE, PROJECT, PAYMENT, CONTRACT }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_user_id", nullable = false)
    private Long recipientUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Topic topic;

    @Column(name = "related_id", nullable = false)
    private Long relatedId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    private LocalDateTime readAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private java.sql.Timestamp createdAt;

    public Long getId() { return id; }
    public Long getRecipientUserId() { return recipientUserId; }
    public void setRecipientUserId(Long v) { this.recipientUserId = v; }
    public Topic getTopic() { return topic; }
    public void setTopic(Topic t) { this.topic = t; }
    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long v) { this.relatedId = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean v) { this.isRead = v; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime v) { this.readAt = v; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
}
