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

}
