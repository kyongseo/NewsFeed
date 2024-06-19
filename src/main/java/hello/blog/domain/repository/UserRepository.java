package hello.blog.domain.repository;


import hello.blog.domain.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String username);
    boolean existsByEmail(String email);
}