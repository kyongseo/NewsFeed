package hello.blog.repository;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}