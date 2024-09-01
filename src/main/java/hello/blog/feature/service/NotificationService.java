package hello.blog.feature.service;

import hello.blog.feature.domain.Notification;
import hello.blog.feature.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void createNotification(String username, String message) {
        Notification notification = new Notification();
        notification.setUsername(username);
        notification.setMessage(message);
        notification.setRead(false);  // 기본값: 읽지 않음
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 특정 ID로 알림을 가져옴
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
    }

    // 사용자에게 보낸 알림 목록을 반환
    public List<Notification> getUnreadNotificationsByUsername(String username) {
        return notificationRepository.findByUsernameAndIsRead(username, false);
    }

    // 사용자에게 보낸 알림 목록을 반환
    public List<Notification> getReadNotificationsByUsername(String username) {
        return notificationRepository.findByUsernameAndIsRead(username, true);
    }
}