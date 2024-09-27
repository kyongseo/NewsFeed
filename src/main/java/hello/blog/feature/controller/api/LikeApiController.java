package hello.blog.feature.controller.api;

import hello.blog.feature.service.NotificationService;
import hello.blog.feature.domain.Post;
import hello.blog.feature.domain.User;
import hello.blog.feature.service.LikeService;
import hello.blog.feature.service.PostService;
import hello.blog.feature.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class LikeApiController {

    private final LikeService likeService;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;

    // 좋아요 버튼을 눌렀을 때
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> like(@PathVariable("postId") Long postId,
                                  Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<Post> postOptional = postService.getPostById(postId);

                if (postOptional.isPresent()) {
                    Post post = postOptional.get();
                    boolean liked = likeService.like(post, user);
                    Long likeCount = likeService.countByPostId(postId);

                    String postOwnerUsername = postService.getPostOwnerUsername(postId);
                    if (!username.equals(postOwnerUsername)) {
                        notificationService.createNotification(postOwnerUsername, username + "님이 게시글에 좋아요를 눌렀습니다.");
                    }

                    return ResponseEntity.ok().body(new LikeResponse(liked, likeCount));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없습니다.");
    }

    @Data
    @AllArgsConstructor
    public static class LikeResponse {
        private boolean liked;
        private Long likeCount;
    }
}