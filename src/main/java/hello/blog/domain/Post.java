package hello.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter @Setter
@NoArgsConstructor
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;

    private Long likes = 0L;

    private String detailLink;


    public Post(String title, String content, String detailLink) {
        this.title = title;
        this.content = content;
        this.detailLink = detailLink;
    }
}