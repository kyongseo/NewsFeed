package hello.blog.controller;

import hello.blog.domain.Reply;
import hello.blog.domain.User;
import hello.blog.repository.UserRepository;
import hello.blog.service.CommentService;
import hello.blog.service.ReplyService;
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

    // 대댓글 생성 처리
    @PostMapping("/replies/create/{commentId}")
    public String createReply(@PathVariable Long commentId,
                              @RequestParam("content") String content,
                              Authentication authentication) {

        // 사용자 인증 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/loginform"; // 로그인 페이지로 리다이렉트
        }

        // 로그인한 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // 사용자 정보를 이용해 사용자 객체 조회
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));

        // 대댓글 생성 및 저장
        Reply reply = new Reply();
        reply.setContent(content);
        reply.setUser(user);

        replyService.saveReply(commentId, reply);

        // 생성 후 해당 댓글이 있는 게시글 페이지로 리다이렉트
        Long postId = commentService.getPostIdByCommentId(commentId);
        return "redirect:/posts/" + postId;
    }

    // 대댓글 수정 처리
    @PostMapping("/replies/edit/{replyId}")
    public String updateReply(@PathVariable Long replyId, @RequestParam("content") String content) {
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
    public String deleteReply(@PathVariable Long replyId) {
        Reply reply = replyService.getReplyById(replyId);
        if (reply != null) {
            Long postId = reply.getComment().getPost().getId();
            replyService.deleteReply(replyId);
            return "redirect:/posts/" + postId; // 삭제 후 댓글이 있는 게시글 페이지로 리다이렉트
        }
        return "redirect:/"; // 예외 처리: 삭제할 대댓글이 없는 경우 메인 페이지로 리다이렉트
    }
}