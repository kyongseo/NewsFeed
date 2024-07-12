package hello.blog.repository;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findTop10ByOrderByCreatedAtDesc(); // 최신 글 10개를 시간순으로 조회하는 메서드
    List<Post> findByTitleContainingOrContentContaining(String title, String content); // 제목, 내용에 검색어가 포함된 게시글 검색

    @Query("SELECT p FROM Post p LEFT JOIN p.likes l GROUP BY p ORDER BY COUNT(l) DESC")
    List<Post> findAllOrderByLikesDesc(); // 좋아요 수 내림차순으로 게시글 조회
}