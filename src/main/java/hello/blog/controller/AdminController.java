package hello.blog.controller;

import hello.blog.domain.Post;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;

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

    // 사용자 영구 삭제
    @PostMapping("/admin/users/{username}/delete")
    public String deleteUser(@PathVariable("username") Long username) {
        userService.deleteUserAdmin(username);
        return "redirect:/admin/userboard";
    }

    // 게시글 영구 삭제
    @PostMapping("/admin/posts/{postId}/delete")
    public String deletePost(@PathVariable("postId") Long postId) {
        postService.deletePostAdmin(postId);
        return "redirect:/admin/postboard";
    }

    // 공지사항 올리기
    @GetMapping("/admin/notice")
    public String createNoticeForm(Model model) {
        model.addAttribute("post", new Post());
        return "admin/noticeboard" ;
    }

    @PostMapping("admin/notice/create")
    public String createNotice(@ModelAttribute Post post) {

        return "redirect:/admin/postboard";
    }
}
