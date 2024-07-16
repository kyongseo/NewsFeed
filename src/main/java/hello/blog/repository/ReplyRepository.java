package hello.blog.repository;

import hello.blog.domain.Comment;
import hello.blog.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByComment(Comment comment);
}
