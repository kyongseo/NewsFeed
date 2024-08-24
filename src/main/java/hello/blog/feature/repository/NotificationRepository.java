package hello.blog.feature.repository;

import hello.blog.feature.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUsername(String username);

//    @Query("SELECT n FROM Notification n WHERE n.userName = :username AND n.sent = false")
//    List<Notification> findUnsentNotificationsByUsername(@Param("username") String username);

    List<Notification> findByUsernameAndIsRead(String username, boolean isRead);

    // '읽음'으로 표시된 알림 중 특정 시간 이전에 생성된 알림을 찾는 메서드
    List<Notification> findByIsReadAndCreatedAtBefore(boolean isRead, LocalDateTime cutoffTime);

    // 또는 '읽음'으로 설정된 알림을 삭제할 수 있는 메서드
    // void deleteByIsReadAndCreatedAtBefore(boolean isRead, LocalDateTime cutoffTime);
}
