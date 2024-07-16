package hello.blog.controller;

import hello.blog.domain.User;
import hello.blog.service.FollowService;
import hello.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    // 팔로우 버튼을 눌렀울 때
    @PostMapping("/follows/{username}")
    public String toggleFollow(@PathVariable("username") String followeeUsername,
                               Authentication authentication,
                               Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String loggedInUsername = authentication.getName();
            Optional<User> loggedInUser = userService.findByUserName(loggedInUsername);
            Optional<User> followeeUser = userService.findByUserName(followeeUsername);

            if (loggedInUser.isPresent() && followeeUser.isPresent()) {
                boolean isFollowing = followService.toggleFollow(loggedInUser.get(), followeeUser.get());
                model.addAttribute("isFollowing", isFollowing);
            }
        }
        return "redirect:/{username}";
    }

    // 내가 팔로우한 사람 목록 보기
    @GetMapping("/{username}/followings")
    public String followingList(@PathVariable("username") String username,
                                Model model) {

        Optional<User> userOptional = userService.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<User> followings = followService.getFollowings(user);
            model.addAttribute("user", user);
            model.addAttribute("followings", followings);
            return "/user/followingList";
        }
        return "redirect:/trending";
    }

    // 나를 팔로우한 사람 목록 보기
    @GetMapping("{username}/followers")
    public String followersList(@PathVariable("username") String username,
                                Model model) {

        Optional<User> userOptional = userService.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<User> followers = followService.getFollowers(user);
            model.addAttribute("user", user);
            model.addAttribute("followers", followers);
            return "/user/followerList";
        }
        return "redirect:/trending";
    }
}