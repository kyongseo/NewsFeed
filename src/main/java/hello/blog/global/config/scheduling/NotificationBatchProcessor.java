package hello.blog.global.config.scheduling;

import hello.blog.feature.domain.Notification;
import hello.blog.feature.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

//@Component
//@RequiredArgsConstructor
//public class NotificationBatchProcessor {
//
//    private final NotificationRepository notificationRepository;
//
////    /**
////     * 주기적으로 호출되어 읽음으로 표시된 알림을 삭제합니다.
////     * 예: 7일 이상 경과한 읽음 알림을 삭제
////     */
////    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
////    public void deleteReadNotifications() {
////        // 현재 시간 기준으로 7일 이상 경과한 읽음 알림을 조회
////       // LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7);
////        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(2);
////        List<Notification> notificationsToDelete = notificationRepository.findByIsReadAndCreatedAtBefore(true, cutoffTime);
////
////        // 알림 삭제
////        notificationRepository.deleteAll(notificationsToDelete);
//
//
//    /**
//     * 주기적으로 호출되어 20분 이상 경과한 읽지 않은 알림을 삭제합니다.
//     * 예: 생성된 지 20분 이상 경과한 읽지 않은 알림을 삭제
//     */
//    @Scheduled(cron = "0 0/5 * * * ?") // 매 5분마다 실행
//    public void deleteUnReadNotifications() {
//        // 현재 시간 기준으로 20분 이상 경과한 알림을 조회
//        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);
//        List<Notification> notificationsToDelete = notificationRepository.findByIsReadAndCreatedAtBefore(false, cutoffTime);
//
//        // 알림 삭제
//        notificationRepository.deleteAll(notificationsToDelete);
//    }
//}
