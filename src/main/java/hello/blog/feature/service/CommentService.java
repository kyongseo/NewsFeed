package hello.blog.feature.service;

import hello.blog.global.config.exception.PostNotFoundException;
import hello.blog.global.config.exception.UserNotFoundException;
import hello.blog.feature.domain.Comment;
import hello.blog.feature.domain.Post;
import hello.blog.feature.domain.User;
import hello.blog.feature.repository.CommentRepository;
import hello.blog.feature.repository.PostRepository;
import hello.blog.feature.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final NotificationService notificationService;

    /**
     * 댓글 추가
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

        Comment savedComment = commentRepository.save(comment);

        // 댓글 작성 후 알림 전송
        sendCommentNotification(post, user, content);

        return savedComment;
    }

    /**
     * 댓글 작성 시 알림 전송
     */
    private void sendCommentNotification(Post post, User commenter, String content) {
        // 게시글 작성자 정보 가져오기
        User postAuthor = post.getUser();

        // 댓글을 작성한 사용자와 게시글 작성자가 동일하지 않을 때만 알림을 보냄
        if (!commenter.getUserId().equals(postAuthor.getUserId())) {
            notificationService.createNotification(postAuthor.getUserName(), commenter.getUserName() + "님이 댓글을 달았습니다: "
            );
        }
    }

    /**
     * 게시글 ID로 해당 게시물 작성자의 사용자 이름 가져오기
     */
    @Transactional(readOnly = true)
    public String getPostOwnerUsername(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다."));
        return post.getUser().getUserName();
    }

    /**
     * 게시물 ID로 해당 게시물의 모든 댓글 조회
     */
    @Transactional
    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    /**
     * 현재 인증된 사용자의 이름 가져오기
     */
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    /**
     * 댓글 수정
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
     */
    @Transactional(readOnly = true)
    public Long getPostIdByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostNotFoundException("해당 댓글을 찾을 수 없습니다."));

        return comment.getPost().getId();
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostNotFoundException("해당 댓글을 찾을 수 없습니다."));

        commentRepository.deleteById(commentId);
    }
}