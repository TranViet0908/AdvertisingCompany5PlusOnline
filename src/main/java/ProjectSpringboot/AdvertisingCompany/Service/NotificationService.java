package ProjectSpringboot.AdvertisingCompany.Service;

import ProjectSpringboot.AdvertisingCompany.Entity.Notification;
import ProjectSpringboot.AdvertisingCompany.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository
                .findByRecipientUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();
    }

    public List<Notification> getNotificationsByUser(Long userId, String type, Boolean unread) {
        var pr = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (Boolean.TRUE.equals(unread))
            return notificationRepository.findByRecipientUserIdAndIsReadOrderByCreatedAtDesc(userId, false, pr).getContent();
        if (type != null && !type.isBlank())
            return notificationRepository.findByRecipientUserIdAndTopicOrderByCreatedAtDesc(userId, Notification.Topic.valueOf(type), pr).getContent();
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId, pr).getContent();
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    public Notification createNotification(Notification n) {
        n.setRead(false);
        n.setReadAt(null);
        return notificationRepository.save(n);
    }

    public Notification updateNotification(Notification details, Long id) {
        Notification n = getNotificationById(id);
        n.setRecipientUserId(details.getRecipientUserId());
        n.setTopic(details.getTopic());
        n.setRelatedId(details.getRelatedId());
        n.setTitle(details.getTitle());
        n.setMessage(details.getMessage());
        n.setRead(details.isRead());
        n.setReadAt(details.isRead() ? (details.getReadAt() != null ? details.getReadAt() : LocalDateTime.now()) : null);
        return notificationRepository.save(n);
    }

    public void deleteNotification(Long id) {
        notificationRepository.delete(getNotificationById(id));
    }

    public long countUnreadByUser(Long userId) {
        return notificationRepository.countByRecipientUserIdAndIsReadFalse(userId);
    }

    public Notification markRead(Long id, Long userId) {
        Notification n = notificationRepository.findByIdAndRecipientUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Notification not found or not yours"));
        if (!n.isRead()) {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);
        }
        return n;
    }

    public void markAllRead(Long userId) {
        List<Notification> list = getNotificationsByUser(userId, null, true);
        for (Notification n : list) {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(list);
    }
}
