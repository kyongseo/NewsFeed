package hello.blog.repository;

import hello.blog.domain.Follow;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByUserAndFollowee(User user, User followee);
}