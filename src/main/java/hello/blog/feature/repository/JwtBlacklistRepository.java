package hello.blog.feature.repository;

import hello.blog.feature.domain.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, Long> {
    boolean existsByToken(String token);
}