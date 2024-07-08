package hello.blog.repository;

import hello.blog.domain.Like;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByPostId(Long postId);

    Optional<Like> findByPostAndUser(Post post, User user);
}