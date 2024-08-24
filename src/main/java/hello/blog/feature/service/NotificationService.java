package hello.blog.feature.service;

import hello.blog.feature.domain.Notification;
import hello.blog.feature.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<Notification> getNotificationsByUsername(String username) {
        return notificationRepository.findByUsername(username);
    }

//    // 알림을 "전송됨"으로 표시하거나 삭제하는 메서드
    public void markAsSent(Notification notification) {
        // 예: 알림을 "전송됨"으로 표시
        notification.setRead(true);
        notificationRepository.save(notification);

        // 또는 알림을 삭제
       // notificationRepository.delete(notification);
    }

    // 특정 ID로 알림을 가져옴
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
    }

    // 알림을 삭제
    public void deleteNotification(Notification notification) {
        notificationRepository.delete(notification);
    }
//    // 알림을 읽은 상태로 업데이트
//    public void markAsRead(Long notificationId) {
//        Notification notification = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
//        notification.setRead(true);
//        notificationRepository.save(notification);
//    }

    // 사용자에게 보낸 알림 목록을 반환
    public List<Notification> getUnreadNotificationsByUsername(String username) {
        return notificationRepository.findByUsernameAndIsRead(username, false);
    }

}
