package hello.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "tags")
@Entity
@Getter @Setter
@NoArgsConstructor
public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "tags")
    Set<Post> Posts = new HashSet<>();

    @Column(nullable = false, unique = false)
    private String name;

    public Tag(String name) {
        this.name = name;
    }
}