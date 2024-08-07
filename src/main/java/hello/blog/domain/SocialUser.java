package hello.blog.domain;

import lombok.Data;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 소셜 정보를 저장하기 위한 객체
 */
@Entity
@Data
@Table(name = "social_user")
public class SocialUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String socialId;
    private String provider;
    private String username;
    private String email;
    private String avatarUrl;
}