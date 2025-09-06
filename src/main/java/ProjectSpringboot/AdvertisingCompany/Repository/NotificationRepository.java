package ProjectSpringboot.AdvertisingCompany.Repository;

import ProjectSpringboot.AdvertisingCompany.Entity.Notification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Notification> findByRecipientUserIdAndIsReadOrderByCreatedAtDesc(Long userId, boolean isRead, Pageable pageable);
    Page<Notification> findByRecipientUserIdAndTopicOrderByCreatedAtDesc(Long userId, Notification.Topic topic, Pageable pageable);
    long countByRecipientUserIdAndIsReadFalse(Long userId);
    Optional<Notification> findByIdAndRecipientUserId(Long id, Long userId);
}
