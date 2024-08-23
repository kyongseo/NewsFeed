package hello.blog.feature.repository;

import hello.blog.feature.domain.Like;
import hello.blog.feature.domain.Post;
import hello.blog.feature.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Long countByPostId(Long postId);

    Optional<Like> findByPostAndUser(Post post, User user);
}