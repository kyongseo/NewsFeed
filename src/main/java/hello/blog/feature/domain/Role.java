package hello.blog.feature.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 역할
 * 사용자와 관리자
 */
@Entity
@Table(name = "roles")
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, unique = true, length = 50)
    private RoleName roleName;

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }
}