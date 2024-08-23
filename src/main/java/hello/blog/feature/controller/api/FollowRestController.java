package hello.blog.feature.controller.api;

import hello.blog.feature.domain.User;
import hello.blog.feature.service.FollowService;
import hello.blog.feature.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowRestController {

    private final FollowService followService;
    private final UserService userService;

    @PostMapping("/{username}")
    public ResponseEntity<String> toggleFollow(@PathVariable("username") String followeeUsername,
                                               Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String loggedInUsername = authentication.getName();
            Optional<User> loggedInUser = userService.findByUserName(loggedInUsername);
            Optional<User> followeeUser = userService.findByUserName(followeeUsername);

            if (loggedInUser.isPresent() && followeeUser.isPresent()) {
                boolean isFollowing = followService.toggleFollow(loggedInUser.get(), followeeUser.get());
                return ResponseEntity.ok(isFollowing ? "Followed" : "Unfollowed");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("노권한");
    }

    // 내가 팔로우한 사람 목록 보기
    @GetMapping("/{username}/followings")
    public ResponseEntity<?> followingList(@PathVariable("username") String username) {
        Optional<User> userOptional = userService.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<User> followings = followService.getFollowings(user);
            return ResponseEntity.ok(followings);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
    }

    // 나를 팔로우한 사람 목록 보기
    @GetMapping("/{username}/followers")
    public ResponseEntity<?> followersList(@PathVariable("username") String username) {
        Optional<User> userOptional = userService.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<User> followers = followService.getFollowers(user);
            return ResponseEntity.ok(followers);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
    }
}
