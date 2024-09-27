package hello.blog.feature.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    // 이메일 전송 메소드
    public void send(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("pokj930@gmail.com");
            mailSender.send(message);

            log.info(">>> Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Error sending email to {}", to, e);
        }
    }
}