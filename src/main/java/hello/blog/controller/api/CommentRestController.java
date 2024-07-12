package hello.blog.controller.api;

import hello.blog.domain.Comment;
import hello.blog.service.CommentService;
import hello.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentRestController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<String> addComment(@PathVariable("postId") Long postId,
                                             @RequestParam String content,
                                             Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            commentService.addComment(postId, content);
            return ResponseEntity.ok("댓글 생성 완료");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("노권한");
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable("commentId") Long commentId,
                                                @RequestParam("content") String content,
                                                Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        Comment updatedComment = commentService.updateComment(commentId, content);
        if (updatedComment != null) {
            return ResponseEntity.ok("댓글 수정 완료");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글 없어ㅠ");
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId,
                                                Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        Long postId = commentService.getPostIdByCommentId(commentId);
        if (postId != null) {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok("댓글 삭제 완료");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글 없어");
        }
    }
}
