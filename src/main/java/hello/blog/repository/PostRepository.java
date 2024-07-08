package hello.blog.repository;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findTop10ByOrderByCreatedAtDesc(); // 최신 글 10개를 시간순으로 조회하는 메서드
}