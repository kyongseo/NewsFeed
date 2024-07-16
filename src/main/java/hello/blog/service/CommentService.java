package hello.blog.service;

import hello.blog.config.exception.CommentNotFoundException;
import hello.blog.config.exception.PostNotFoundException;
import hello.blog.config.exception.UserNotFoundException;
import hello.blog.domain.Comment;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.CommentRepository;
import hello.blog.repository.PostRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 추가 기능
     * 댓글이 작성될 게시물의 ID
     * @param content  댓글 내용
     * @return 추가된 Comment 객체
     */
    @Transactional
    public Comment addComment(Long postId, String content) {
        String username = getCurrentUsername();
        if (username == null) {
            throw new IllegalStateException("로그인된 사용자만 댓글을 작성할 수 있습니다.");
        }

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다. username: " + username));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 포스트를 찾을 수 없습니다. postId: " + postId));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);

        return commentRepository.save(comment);
    }

    /**
     * 게시물 ID로 해당 게시물의 모든 댓글 조회
     *
     * @param postId 게시물 ID
     * @return 조회된 Comment 리스트
     */
    @Transactional
    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    /**
     * 현재 인증된 사용자의 이름 가져오기
     *
     * @return 현재 인증된 사용자 이름
     */
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    /**
     * 댓글 수정 기능
     *
     * @param commentId   수정할 댓글의 ID
     * @param newContent  수정할 내용
     * @return 수정된 Comment 객체
     */
    @Transactional
    public Comment updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostNotFoundException("해당 댓글을 찾을 수 없습니다."));

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    /**
     * 댓글의 게시물 ID 조회
     *
     * @param commentId 댓글의 ID
     * @return 댓글이 속한 게시물의 ID
     */
    @Transactional(readOnly = true)
    public Long getPostIdByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostNotFoundException("해당 댓글을 찾을 수 없습니다."));

        return comment.getPost().getId();
    }
    /**
     * 댓글 삭제 기능
     *
     * @param commentId 삭제할 댓글의 ID
     */
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostNotFoundException("해당 댓글을 찾을 수 없습니다."));

        commentRepository.deleteById(commentId);
    }

}