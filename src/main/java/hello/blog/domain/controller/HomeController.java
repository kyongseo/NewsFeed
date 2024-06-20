package hello.blog.domain.controller;

import hello.blog.domain.domain.Blog;
import hello.blog.domain.domain.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomePage(Model model) {
        List<Post> blogPosts = Arrays.asList(
                new Post("제목1", "내용1", "/detail/1"),
                new Post("제목2", "내용2", "/detail/2"),
                new Post("제목3", "내용3", "/detail/3"),
                new Post("제목4", "내용4", "/detail/4"),
                new Post("제목5", "내용5", "/detail/5")
        );
        model.addAttribute("blogPosts", blogPosts);
        return "home";
    }
}