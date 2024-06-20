package hello.blog.controller;
import hello.blog.domain.Blog;
import hello.blog.domain.User;
import hello.blog.service.BlogService;
import hello.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/blogs")
public class BlogController {
    private final BlogService blogService;
    private final UserService userService;

    @GetMapping("/create")
    public String createBlogForm(@CookieValue(value = "username", defaultValue = "") String username, Model model) {
        if (username.isEmpty()) {
            return "redirect:/loginform";
        }
        model.addAttribute("blog", new Blog());
        return "/blog/create";
    }

    @PostMapping("/create")
    public String createBlog(@RequestParam String title,
                             @CookieValue(value = "username", defaultValue = "") String username) {
        if (username.isEmpty()) {
            return "redirect:/loginform";
        }
        Optional<User> userOptional = userService.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            blogService.createBlog(title, user);
            return "redirect:/";
        }
        return "redirect:/loginform";
    }
}