package hello.blog.feature.controller;

import hello.blog.feature.domain.Notification;
import hello.blog.feature.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * 로그인한 사용자의 읽지 않은 알림
     * @param model
     * @param authentication
     * @return
     */
    //@PreAuthorize("hasRole('USER')")
    @GetMapping
    public String getNotifications(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/loginform"; // 로그인 페이지로 리다이렉트
        }

        String username = authentication.getName();
        List<Notification> notifications = notificationService.getUnreadNotificationsByUsername(username);

        model.addAttribute("notifications", notifications);

        return "notifications";
    }

    // 알림을 읽음 상태로 변경하는 메서드
    //@PreAuthorize("hasRole('USER')")
    @PostMapping("/mark-as-sent/{id}")
    public String markAsRead(@PathVariable("id") Long id) {
        Notification notification = notificationService.getNotificationById(id);
        if (notification != null) {
            notificationService.markAsRead(notification);
            //notificationService.markAsRead(id);
        }
        return "redirect:/notifications"; // 알림 목록 페이지로 리다이렉트
    }
}
