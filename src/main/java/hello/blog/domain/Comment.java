package hello.blog.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

//@Entity
//@Table(name = "comments")
//@Getter @Setter
public class Comment {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "post_id", nullable = false)
//    private Post post;
//
//    @Column(nullable = false)
//    private String content;
//
//    @CreatedDate
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdA = LocalDateTime.now();
}
