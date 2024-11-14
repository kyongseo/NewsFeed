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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class LikeApiController {

    private final LikeService likeService;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final RedissonClient redissonClient;

    // 좋아요 버튼을 눌렀을 때
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> like(@PathVariable("postId") Long postId,
                                  Authentication authentication) {

        // Redis 분산 락을 postId에 기반하여 생성
        RLock lock = redissonClient.getLock("post-like-lock:" + postId);

        try {
            // 락을 5초 동안 시도하고, 락을 얻으면 3초 후 자동 해제
            if (lock.tryLock(5, 3, TimeUnit.SECONDS)) {
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
            } else {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("잠금 획득에 실패했습니다. 잠시 후 다시 시도해 주세요.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("잠금 중 오류가 발생했습니다.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class LikeResponse {
        private boolean liked;
        private Long likeCount;
    }
}