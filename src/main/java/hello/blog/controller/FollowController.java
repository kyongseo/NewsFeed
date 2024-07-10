package hello.blog.controller;

import hello.blog.domain.Follow;
import hello.blog.domain.Post;
import hello.blog.domain.User;
//import hello.blog.service.FollowService;
import hello.blog.repository.FollowRepository;
import hello.blog.service.FollowService;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
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

    @GetMapping("{username}/followings")
    public String followingList(@PathVariable("username") String username,
                                Model model) {

        return "/user/followList";
    }
}