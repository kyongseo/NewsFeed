package hello.blog.feature.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "likes")
@Getter @Setter
public class Like {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 한개의 포스트에 좋아요 여러개

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean likeCount;  // true면 좋아요, false면 좋아요 취소
}