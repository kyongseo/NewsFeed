package hello.blog.repository;

import hello.blog.domain.Blog;
import hello.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    Optional<Blog> findBlogByUser(User user);
}