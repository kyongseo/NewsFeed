package hello.blog.feature.repository;

import hello.blog.feature.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUsername(String username);

//    @Query("SELECT n FROM Notification n WHERE n.userName = :username AND n.sent = false")
//    List<Notification> findUnsentNotificationsByUsername(@Param("username") String username);

    List<Notification> findByUsernameAndIsRead(String username, boolean isRead);
}
