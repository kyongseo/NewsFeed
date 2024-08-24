package hello.blog.feature.controller.api;

import hello.blog.feature.service.NotificationService;
import hello.blog.feature.domain.Notification;
import hello.blog.feature.domain.User;
import hello.blog.feature.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/notifications")
//public class NotificationRestController {
//
//    private final NotificationService notificationService;
//    private final UserService userService;
//
//    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
//
//    @GetMapping("/stream")
//    public SseEmitter streamNotifications(Authentication authentication) {
//        SseEmitter emitter = new SseEmitter(60000L); // 60초 타임아웃 설정
//        emitters.add(emitter);
//
//        emitter.onCompletion(() -> emitters.remove(emitter));
//        emitter.onTimeout(() -> {
//            emitters.remove(emitter);
//            emitter.complete();
//        });
//        emitter.onError((e) -> {
//            emitters.remove(emitter);
//            emitter.completeWithError(e);
//        });
//
//        // 사용자 인증 확인
//        if (authentication == null) {
//            emitter.complete();
//            return emitter;
//        }
//
//        String username = authentication.getName();
//        Optional<User> userOptional = userService.findByUserName(username);
//
//        if (!userOptional.isPresent()) {
//            emitter.complete();
//            return emitter;
//        }
//
//        String user = userOptional.get().getUserName();
//
//        // 새로운 쓰레드에서 알림을 전송
//        new Thread(() -> {
//            try {
//                while (true) {
//                    if (emitters.contains(emitter)) {
//                        List<Notification> notifications = notificationService.getNotificationsByUsername(user);
//                        if (!notifications.isEmpty()) {
//                            for (Notification notification : notifications) {
//                                emitter.send(SseEmitter.event()
//                                        .name("notification")
//                                        .data(notification.getMessage()));
//                            }
//                        }
//                    } else {
//                        break; // emitter가 이미 완료된 경우 반복문 종료
//                    }
//                    Thread.sleep(10000); // 10초마다 알림을 체크
//                }
//            } catch (Exception e) {
//                emitter.completeWithError(e);
//            }
//        }).start();
//
//        return emitter;
//    }
//}

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private static final Logger log = LoggerFactory.getLogger(NotificationRestController.class);
    private final NotificationService notificationService;
    private final UserService userService;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/stream")
    public SseEmitter streamNotifications(Authentication authentication) {
        SseEmitter emitter = new SseEmitter(60000L); // 60초 타임아웃 설정
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            emitter.complete();
        });
        emitter.onError((e) -> {
            emitters.remove(emitter);
            emitter.completeWithError(e);
        });

        // 사용자 인증 확인
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

        String user = userOptional.get().getUserName();

        // 새로운 쓰레드에서 알림을 전송
        new Thread(() -> {
            try {
                while (true) {
                    if (emitters.contains(emitter)) {
                        List<Notification> notifications = notificationService.getUnreadNotificationsByUsername(user);
                        if (!notifications.isEmpty()) {
                            for (Notification notification : notifications) {
                                emitter.send(SseEmitter.event()
                                        .name("notification")
                                        .data(notification.getMessage()));

                                // 알림을 읽음 상태로 업데이트
                                notificationService.markAsRead(notification.getId());
                            }
                        }
                    } else {
                        break; // emitter가 이미 완료된 경우 반복문 종료
                    }
                    Thread.sleep(10000); // 10초마다 알림을 체크
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                //emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}
