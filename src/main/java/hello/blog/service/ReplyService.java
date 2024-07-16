package hello.blog.service;

import hello.blog.domain.Comment;
import hello.blog.domain.Reply;
import hello.blog.repository.CommentRepository;
import hello.blog.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;

    // 대댓글 저장
    public Reply saveReply(Long commentId, Reply reply) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다: " + commentId));

        // 대댓글을 해당 댓글에 연결
        reply.setComment(comment);

        return replyRepository.save(reply);
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