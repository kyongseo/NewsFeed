package hello.blog.service;

import hello.blog.config.exception.PostNotFoundException;
import hello.blog.config.exception.UserNotFoundException;
import hello.blog.domain.Comment;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.CommentRepository;
import hello.blog.repository.PostRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment addComment(Long postId, String content) {
        String username = getCurrentUsername();
        if (username == null) {
            throw new IllegalStateException("로그인된 사용자만 댓글을 작성할 수 있습니다.");
        }

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 포스트를 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);

        return commentRepository.save(comment);
    }

    @Transactional
    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
}
