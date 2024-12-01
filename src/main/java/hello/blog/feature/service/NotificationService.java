package hello.blog.feature.service;

import hello.blog.feature.domain.Notification;
import hello.blog.feature.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 1분 타임아웃
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    public void sendNotification(Long userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                log.error("Failed to send SSE notification", e);
                emitters.remove(userId);
            }
        }
    }

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