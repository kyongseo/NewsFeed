package hello.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 역할
 * 사용자와 관리자
 */
@Entity
@Table(name = "roles")
@Getter@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleName roleName;

}