package hello.blog.feature.controller;

import hello.blog.feature.service.NotificationService;
import hello.blog.feature.domain.Reply;
import hello.blog.feature.domain.User;
import hello.blog.feature.repository.UserRepository;
import hello.blog.feature.service.CommentService;
import hello.blog.feature.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;
    private final CommentService commentService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 대댓글 생성 처리
    @PostMapping("/replies/create/{commentId}")
    public String createReply(@PathVariable("commentId") Long commentId,
                              @RequestParam("content") String content,
                              Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/loginform";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        // 대댓글 생성 및 저장, 알림 전송
        replyService.createReplyWithNotification(commentId, content, user);

        Long postId = commentService.getPostIdByCommentId(commentId);
        return "redirect:/posts/" + postId;
    }


    // 대댓글 수정 처리
    @PostMapping("/replies/edit/{replyId}")
    public String updateReply(@PathVariable("replyId") Long replyId,
                              @RequestParam("content") String content) {
        Reply existingReply = replyService.getReplyById(replyId);
        if (existingReply != null) {
            existingReply.setContent(content);
            replyService.updateReply(replyId, existingReply);
        }

        // 수정 후 해당 댓글이 있는 게시글 페이지로 리다이렉트
        Long postId = existingReply.getComment().getPost().getId();
        return "redirect:/posts/" + postId;
    }

    // 대댓글 삭제 처리
    @PostMapping("/replies/delete/{replyId}")
    public String deleteReply(@PathVariable("replyId") Long replyId) {
        Reply reply = replyService.getReplyById(replyId);
        if (reply != null) {
            Long postId = reply.getComment().getPost().getId();
            replyService.deleteReply(replyId);
            return "redirect:/posts/" + postId;
        }
        return "redirect:/trending";
    }
}