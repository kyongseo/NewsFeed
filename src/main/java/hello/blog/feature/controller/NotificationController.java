package hello.blog.feature.controller;

import hello.blog.feature.domain.Notification;
import hello.blog.feature.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * 로그인한 사용자의 읽지 않은 알림
     */
    @GetMapping("/notifications")
    public String getNotifications(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authentication.getName();

        // 읽지 않은 알림과 읽은 알림을 구분하여 조회
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationsByUsername(username);
        List<Notification> readNotifications = notificationService.getReadNotificationsByUsername(username);

        // 모델에 읽지 않은 알림과 읽은 알림을 추가
        model.addAttribute("unreadNotifications", unreadNotifications);
        model.addAttribute("readNotifications", readNotifications);

        return "notifications"; // HTML 템플릿 이름
    }

    // 알림을 읽음 상태로 변경하는 메서드
    @PostMapping("/api/notifications/mark-as-read/{id}")
    @ResponseBody
    public ResponseEntity<String> markAsRead(@PathVariable("id") Long id) {
        Notification notification = notificationService.getNotificationById(id);
        System.out.println(notification);
        if (notification != null) {
            notificationService.markAsRead(id); // 알림을 읽음 상태로 변경
            return ResponseEntity.ok("Notification marked as read");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found");
        }
    }
}