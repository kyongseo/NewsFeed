package hello.blog.global.config.scheduling;

import hello.blog.feature.controller.api.NotificationRestController;
import hello.blog.feature.domain.Notification;
import hello.blog.feature.repository.NotificationRepository;
import hello.blog.feature.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
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
    private final EmailService emailService;

    // RetryTemplate 빈 추가 (재시도 정책 설정)
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // 최대 재시도 횟수 3번
        retryTemplate.setRetryPolicy(retryPolicy);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000); // 재시도 간격 2초
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

    // Job - 배치 작업의 전체 실행
    @Bean
    public Job deleteNotificationsJob(Step deleteUnreadNotificationsStep) {
        return new JobBuilder("deleteNotificationsJob", jobRepository)
                .start(deleteUnreadNotificationsStep)
                .build();
    }

    // Step - 배치 작업 내 실행되는 개별 처리
    @Bean
    public Step deleteUnreadNotificationsStep() {
        log.info(">>> Creating deleteUnreadNotificationsStep");
        return new StepBuilder("deleteUnreadNotificationsStep", jobRepository)
                .tasklet(checkUnreadNotificationsTasklet(), platformTransactionManager)
                .build();
    }

    // Tasklet 1 - 30일 이상 읽지 않은 알림 삭제
    @Bean
    public Tasklet deleteUnreadNotificationsTasklet() {
        return (contribution, chunkContext) -> {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(30);
            List<Notification> notificationsToDelete = notificationRepository.findByIsReadAndCreatedAtBefore(false, cutoffTime);
            notificationRepository.deleteAll(notificationsToDelete);
            return RepeatStatus.FINISHED;
        };
    }

    // Tasklet 2 - 7일 이상 읽지 않은 알림 확인 후 사용자에게 SSE 알림 전송
    @Bean
    public Tasklet checkUnreadNotificationsTasklet() {
        return (contribution, chunkContext) -> {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7);
            List<Long> usersWithUnreadNotifications = notificationRepository.findUsersWithUnreadNotifications(cutoffTime);

            for (Long userId : usersWithUnreadNotifications) {
                notificationRestController.sendUnreadNotificationAlert(userId); // 재알림 전송
            }
            return RepeatStatus.FINISHED;
        };
    }

    // Tasklet 3 - 재시도 로직
    @Bean
    public Tasklet retryableTasklet() {
        return (contribution, chunkContext) -> retryTemplate().execute(context -> {
            try {
                LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7);

                // 읽지 않은 알림 확인 후 SSE 알림 전송
                List<Long> usersWithUnreadNotifications = notificationRepository.findUsersWithUnreadNotifications(cutoffTime);
                for (Long userId : usersWithUnreadNotifications) {
                    notificationRestController.sendUnreadNotificationAlert(userId); // 재알림 전송
                }

            } catch (Exception e) {
                log.error("Error during notification processing", e);
                throw e; // 재시도를 위해 예외를 던짐
            }
            return RepeatStatus.FINISHED;
        });
    }

    // StepExecutionListener - 배치 작업 실패 시 관리자에게 알림 전송
    @Bean
    public StepExecutionListener batchStepFailureListener() {
        return new StepExecutionListener() {

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                if (stepExecution.getStatus() == BatchStatus.FAILED) {

                    // 관리자가 실패를 인지할 수 있도록 알림 전송
                    String errorMessage = "Batch step failed after all retries. Please investigate the issue.";
                    try {
                        sendAdminAlert("Batch Failure: deleteUnreadNotificationsStep", errorMessage);
                    } catch (Exception e) {
                        log.error("Error sending admin alert", e);
                    }
                }
                return stepExecution.getExitStatus();
            }
        };
    }

    private void sendAdminAlert(String subject, String message) {
        emailService.send("pokj930@naver.com", subject, message);
    }
}