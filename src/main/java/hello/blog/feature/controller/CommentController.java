package hello.blog.feature.controller;

import hello.blog.feature.service.NotificationService;
import hello.blog.feature.domain.Comment;
import hello.blog.feature.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final NotificationService notificationService; // 알림 서비스 추가

    @PostMapping("/post/{postId}/comments")
    public String addComment(@PathVariable("postId") Long postId,
                             @RequestParam("content") String content,
                             Authentication authentication,
                             Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            // 댓글을 추가합니다.
            commentService.addComment(postId, content);

//            // 게시글 작성자에게 알림
//            String postOwnerUsername = commentService.getPostOwnerUsername(postId);
//            if (!username.equals(postOwnerUsername)) {
//                notificationService.createNotification(postOwnerUsername, username + "님이 댓글을 달았습니다.");
//            }
            return "redirect:/posts/" + postId;
        } else {
            return "redirect:/loginform";
        }
    }

    // 댓글 수정
    @PostMapping("/comment/{commentId}/update")
    public String updateComment(@PathVariable("commentId") Long commentId,
                                @RequestParam("content") String content) {
        Comment updatedComment = commentService.updateComment(commentId, content);
        Long postId = updatedComment.getPost().getId();
        return "redirect:/posts/" + postId;
    }

    // 댓글 삭제
    @PostMapping("/comment/{commentId}/delete")
    public String deleteComment(@PathVariable("commentId") Long commentId) {
        Long postId = commentService.getPostIdByCommentId(commentId); // 댓글이 속한 포스트의 ID 가져오기

        commentService.deleteComment(commentId); // 댓글 삭제 처리

        // 삭제 후 해당 포스트 페이지로 리다이렉트
        return "redirect:/posts/" + postId;
    }
}
