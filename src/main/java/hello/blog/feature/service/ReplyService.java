package hello.blog.feature.service;

import hello.blog.feature.domain.Comment;
import hello.blog.feature.domain.Reply;
import hello.blog.feature.domain.User;
import hello.blog.feature.repository.CommentRepository;
import hello.blog.feature.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService; // 알림 서비스 추가

    // 대댓글 생성 및 알림 전송
    @Transactional
    public void createReplyWithNotification(Long commentId, String content, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentId));

        // 대댓글 생성
        Reply reply = new Reply();
        reply.setContent(content);
        reply.setUser(user);
        reply.setComment(comment);
        replyRepository.save(reply);

        // 대댓글 작성자와 원 댓글 작성자가 다를 때 알림을 보냅니다.
        User commentOwner = comment.getUser(); // 원 댓글 작성자
        if (!user.getUserName().equals(commentOwner.getUserName())) {
            notificationService.createNotification(commentOwner.getUserName(), user.getUserName() + "님이 대댓글을 달았습니다.");
        }
    }

    // 대댓글 조회
    public Reply getReplyById(Long replyId) {
        return replyRepository.findById(replyId)
                .orElse(null);
    }

    // 대댓글 수정
    public Reply updateReply(Long replyId, Reply updatedReply) {
        Reply existingReply = replyRepository.findById(replyId)
                .orElse(null);
        if (existingReply != null) {
            existingReply.setContent(updatedReply.getContent());
            return replyRepository.save(existingReply);
        }
        return null;
    }

    // 대댓글 삭제
    public void deleteReply(Long replyId) {
        replyRepository.deleteById(replyId);
    }
}