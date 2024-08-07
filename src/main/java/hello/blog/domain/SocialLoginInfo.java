package hello.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 회원가입을 위한 객체
 */
@Entity
@Table(name = "social_login_info")
@Getter @Setter
public class SocialLoginInfo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;

    @Column(nullable = false)
    private String socialId;

    private LocalDateTime createdAt;
    private String uuid;

    public SocialLoginInfo() {
        // 소셜로그인한 사용자의 시간이랑 UUID를 만듦
        // 소셜로그인한 이후에 특정 시간까지만 추가작업을 가능하도록 하려고!
        this.createdAt = LocalDateTime.now();
        this.uuid = UUID.randomUUID().toString();
    }
}
