package hello.blog.feature.controller;

import hello.blog.feature.domain.Comment;
import hello.blog.feature.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 추가
    @PostMapping("/post/{postId}/comments")
    public String addComment(@PathVariable("postId") Long postId,
                             @RequestParam("content") String content,
                             Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            commentService.addComment(postId, content);
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
        Long postId = commentService.getPostIdByCommentId(commentId);

        commentService.deleteComment(commentId);

        return "redirect:/posts/" + postId;
    }
}
