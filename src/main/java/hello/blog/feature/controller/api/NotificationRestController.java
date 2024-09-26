package hello.blog.feature.controller.api;

import hello.blog.feature.repository.NotificationRepository;
import hello.blog.feature.service.NotificationService;
import hello.blog.feature.domain.Notification;
import hello.blog.feature.domain.User;
import hello.blog.feature.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private static final Logger log = LoggerFactory.getLogger(NotificationRestController.class);
    private final NotificationService notificationService;
    private final UserService userService;
    private final NotificationRepository notificationRepository;

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping("/stream")
    public SseEmitter streamNotifications(Authentication authentication) {
        SseEmitter emitter = new SseEmitter(60000L); // 60초 타임아웃 설정

        if (authentication == null) {
            emitter.complete();
            return emitter;
        }

        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUserName(username);

        if (!userOptional.isPresent()) {
            emitter.complete();
            return emitter;
        }

        // 중복 방지: 동일한 사용자가 이미 구독 중이면 기존 Emitter 제거
        emitters.compute(username, (key, existingEmitter) -> {
            if (existingEmitter != null) {
                existingEmitter.complete(); // 기존 emitter 완료
            }
            return emitter; // 새로운 emitter 추가
        });

        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onTimeout(() -> {
            emitters.remove(username);
            emitter.complete();
        });
        emitter.onError((e) -> {
            emitters.remove(username);
            emitter.completeWithError(e);
        });

        // 새로운 쓰레드에서 알림을 전송
        new Thread(() -> {
            try {
                while (true) {
                    if (emitters.containsKey(username)) {
                        List<Notification> notifications = notificationService.getUnreadNotificationsByUsername(username);
                        if (!notifications.isEmpty()) {
                            for (Notification notification : notifications) {
                                emitter.send(SseEmitter.event()
                                        .name("notification")
                                        .data(notification.getMessage()));
                            }
                            break;
                        }
                    } else {
                        break; // emitter가 이미 완료된 경우 반복문 종료
                    }
                    Thread.sleep(10000); // 10초마다 알림을 체크
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                emitters.remove(username);
                // emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    @GetMapping("/notifications/subscribe")
    public SseEmitter subscribe(@RequestParam Long userId) {
        return notificationService.createEmitter(userId);
    }

    // 사용자의 읽지 않은 알림에 대해 알림 전송
    public void sendUnreadNotificationAlert(Long userId) {
        boolean hasUnreadNotifications = notificationRepository.existsByUserIdAndIsRead(userId, false);
        if (hasUnreadNotifications) {
            notificationService.sendNotification(userId, "아직 안읽은 알림이 있습니다.");
        }
    }
}