package hello.blog.controller;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final PostService postService;

    // 메인 홈 화면
    @GetMapping("/")
    public String showHomePage(Model model, @CookieValue(value = "username", defaultValue = "") String username) {
        if (!username.isEmpty()) {
            Optional<User> userOptional = userService.findByUserName(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("nickname", user.getUserNick());
                model.addAttribute("username", user.getUserName());
            }
        } else {
            model.addAttribute("username", "");
        }

        Iterable<Post> blogPosts = postService.getAllPosts();
        model.addAttribute("blogPosts", blogPosts);
        return "home";
    }

    @GetMapping("/logout")
    public String logoutUser (HttpServletResponse response){
        Cookie cookie = new Cookie("username", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
