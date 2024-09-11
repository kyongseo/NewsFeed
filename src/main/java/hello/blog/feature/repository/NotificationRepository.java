package hello.blog.feature.repository;

import hello.blog.feature.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUsername(String username);

    List<Notification> findByUsernameAndIsRead(String username, boolean isRead);

    // '읽음'으로 표시된 알림 중 특정 시간 이전에 생성된 알림을 찾는 메서드
    List<Notification> findByIsReadAndCreatedAtBefore(boolean isRead, LocalDateTime cutoffTime);

    // 특정 시간 이전에 생성된 읽지 않은 알림이 있는 사용자 목록을 반환하는 메서드
    @Query("SELECT DISTINCT n.id FROM Notification n WHERE n.isRead = false AND n.createdAt < :cutoffTime")
    List<Long> findUsersWithUnreadNotifications(@Param("cutoffTime") LocalDateTime cutoffTime);

    // 특정 사용자가 읽지 않은 알림이 존재하는지 확인하는 메서드
    boolean existsByUserIdAndIsRead(Long userId, boolean isRead);
}
