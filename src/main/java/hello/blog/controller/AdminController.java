package hello.blog.controller;

import hello.blog.domain.Post;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PostService postService;

    // 사용자 목록
    @GetMapping("/admin/userboard")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("posts", postService.getAllPosts());
        return "admin/userboard";
    }

    // 게시글 목록
    @GetMapping("/admin/postboard")
    public String showAdminDashboard(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("posts", postService.getAllPosts());
        return "admin/postboard";
    }

    // 특정 사용자의 게시글
    @GetMapping("/admin/{username}/posts")
    public String getUserPosts(@PathVariable("username") String username, Model model) {
        Set<Post> userPosts = userService.getUserPosts(username);
        model.addAttribute("username", username);
        model.addAttribute("posts", userPosts);
        return "admin/blogs";
    }
}
