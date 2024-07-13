package hello.blog.controller;

import hello.blog.domain.Like;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.LikeRepository;
import hello.blog.service.LikeService;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final PostService postService;
    private final UserService userService;

    // 좋아요 버튼을 눌렀을 때
    @PostMapping("/posts/{postId}/like")
    public String like(@PathVariable("postId") Long postId, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<Post> postOptional = postService.getPostById(postId);

                postOptional.ifPresent(post -> likeService.like(post, user));
            }
        }

        return "redirect:/posts/" + postId;
    }
}