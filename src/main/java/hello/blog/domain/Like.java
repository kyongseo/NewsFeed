package hello.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "likes")
@Getter @Setter
public class Like {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // FetchType.EAGER
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 한개의 포스트에 좋아요 여러개

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 한명의 유저가 좋아요 여러개 할 수 있음

    private boolean likeCount;  // true면 좋아요, false면 좋아요 취소
}