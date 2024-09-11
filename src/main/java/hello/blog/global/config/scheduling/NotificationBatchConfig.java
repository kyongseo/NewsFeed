package hello.blog.global.config.scheduling;

import hello.blog.feature.controller.api.NotificationRestController;
import hello.blog.feature.domain.Notification;
import hello.blog.feature.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableBatchProcessing
@Slf4j
@RequiredArgsConstructor
public class NotificationBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final NotificationRepository notificationRepository;
    private final NotificationRestController notificationRestController;

    // 배치 작업의 전체 실행
    @Bean
    public Job deleteNotificationsJob(Step deleteUnreadNotificationsStep) {
        log.info(">>> Creating deleteNotificationsJob");
        return new JobBuilder("deleteNotificationsJob", jobRepository)
                .start(deleteUnreadNotificationsStep)
                .build();
    }

    // 배치 작업 내 실행되는 개별 처리
    @Bean
    public Step deleteUnreadNotificationsStep() {
        log.info(">>> Creating deleteUnreadNotificationsStep");
        return new StepBuilder("deleteUnreadNotificationsStep", jobRepository)
                .tasklet(deleteUnreadNotificationsTasklet(), platformTransactionManager)
                .build();
    }

    // 10분 이상 읽지 않은 알림 삭제
    @Bean
    public Tasklet deleteUnreadNotificationsTasklet() {
        return (contribution, chunkContext) -> {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);
            List<Notification> notificationsToDelete = notificationRepository.findByIsReadAndCreatedAtBefore(false, cutoffTime);
            notificationRepository.deleteAll(notificationsToDelete);
            log.info("Deleted unread notifications older than 10 minutes");
            return RepeatStatus.FINISHED;
        };
    }

    // 읽지 않은 알림 확인 후 사용자에게 SSE 알림 전송
    @Bean
    public Tasklet checkUnreadNotificationsTasklet() {
        return (contribution, chunkContext) -> {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);
            List<Long> usersWithUnreadNotifications = notificationRepository.findUsersWithUnreadNotifications(cutoffTime);

            for (Long userId : usersWithUnreadNotifications) {
                notificationRestController.sendUnreadNotificationAlert(userId);
            }

            log.info("Sent unread notification alerts to users");
            return RepeatStatus.FINISHED;
        };
    }
}