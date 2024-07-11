package hello.blog.controller;
import hello.blog.domain.Comment;
import hello.blog.domain.Post;
import hello.blog.service.CommentService;
import hello.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final PostService postService;
    private final CommentService commentService;

    @PostMapping("/post/{postId}/comments")
    public String addComment(@PathVariable("postId") Long postId,
                             @RequestParam String content,
                             Authentication authentication,
                             Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            commentService.addComment(postId, content);
            Optional<Post> post = postService.getPostById(postId);

            model.addAttribute("postId", postId);

            return "redirect:/posts/{postId}";
        } else {
            return "redirect:/loginform";
        }
    }

    // 댓글 수ㅠ정
    @PostMapping("/comment/{commentId}/update")
    public String updateComment(@PathVariable("commentId") Long commentId,
                                @RequestParam String content) {
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
