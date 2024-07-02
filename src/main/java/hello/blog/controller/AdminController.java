package hello.blog.controller;

import hello.blog.service.PostService;
import hello.blog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;


//@Controller
//@RequiredArgsConstructor
public class AdminController {

//    private final UserService userService;
//    private final PostService postService;
//
//
//    @GetMapping("/admin")
//    public String adminPage(Model model) {
//        model.addAttribute("users", postService.getAllPosts());
//        return "admin/adminpage";
//    }
//
//    @GetMapping("/admin/dashboard")
//    public String showAdminDashboard(Model model) {
//        model.addAttribute("users", userService.findAllUsers());
//        model.addAttribute("posts", postService.getAllPosts());
//        return "admin/dashboard";
//    }

    // 관리자 대시보드 -- 시큐리티 배으고 나중에 구현..
//    @GetMapping("/admin/dashboard")
//    public String showAdminDashboard(Model model) {
//        model.addAttribute("username", "admin");
//        return "redirect:/";
//    }

//    @GetMapping("/@{username}/posts")
//    public String getUserPosts(@PathVariable("username") String username, Model model) {
//        Set<Post> userPosts = userService.getUserPosts(username);
//        model.addAttribute("username", username);
//        model.addAttribute("posts", userPosts);
//        return "blog";
//    }
}
