package hello.blog.global.config.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationBatchScheduler {

    private final JobLauncher jobLauncher; // 정의된 job 을 주기적 실행
    private final Job deleteNotificationsJob;

    @Scheduled(cron = "0 0 * * * ?", zone = "Asia/Seoul") // 매 5분마다 실행
    public void runDeleteUnreadNotificationsJob() {
        try {
            jobLauncher.run(deleteNotificationsJob, new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters());
            log.info("Notification deletion job executed successfully.");
        } catch (Exception e) {
            log.error("Failed to execute notification deletion job", e);
        }
    }
}