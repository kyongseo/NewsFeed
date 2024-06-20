package hello.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blogs")
@Getter @Setter
public class Blog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @OneToOne
    @JoinColumn(name = "login_id", unique = true)
    private User user;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();
}
