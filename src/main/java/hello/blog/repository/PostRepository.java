package hello.blog.repository;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Containing : LIKE '%keyword% --> 부분 일치
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findTopByOrderByCreatedAtDesc(); // 최신 글 정렬 내림차순

    List<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable); // 제목, 내용에 검색어가 포함된 게시글 검색

    @Query("SELECT p FROM Post p LEFT JOIN p.likes l GROUP BY p ORDER BY COUNT(l) DESC")
    List<Post> findAllOrderByLikesDesc(); // 좋아요 수 정렬 내림차순

    List<Post> findByUserAndIsDraftTrue(User user); // 게시글 임시저장 상태

    List<Post> findByUserIn(List<User> users); // 팔로우한 사용자 보이도록

    List<Post> findByUserUserNameContaining(String user); // 작성자가 포함된 게시글 검색

    // 조회수
    @Modifying
    @Query("UPDATE Post p SET p.view = p.view + 1 WHERE p.id = :id")
    int updateView(@Param("id") Long id);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName")
    List<Post> findByTagName(@Param("tagName") String tagName);
}