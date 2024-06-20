package hello.blog.controller;

import hello.blog.domain.Post;
import hello.blog.repository.PostRepository;
import hello.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;

    @GetMapping("/create")
    public String createPostForm(@CookieValue(value = "username", defaultValue = "") String username, Model model) {
        if (username.isEmpty()) {
            return "redirect:/loginform";
        }
        model.addAttribute("post", new Post());
        return "/post/createPost";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam String title,
                             @RequestParam String content,
                             @CookieValue(value = "username", defaultValue = "") String username) {
        if (username.isEmpty()) {
            return "redirect:/loginform";
        }
        postService.createPost(title, content, username);
        return "redirect:/";
    }

    @GetMapping("/{postId}")
    public String getPost(@PathVariable Long postId, Model model) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            return "post/viewPost";
        }
        return "redirect:/";
    }
}