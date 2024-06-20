package hello.blog.controller;
import hello.blog.domain.Blog;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.service.BlogService;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
public class BlogController {
//    private final BlogService blogService;
//    private final PostService postService;
//    private final UserService userService;
//
//    @GetMapping("/blogs/{username}")
//    public String getBlogByUsername(@PathVariable("username") String username, Model model) {
//        Blog blog = blogService.getBlogByUsername(username);
//        model.addAttribute("blog", blog);
//        model.addAttribute("posts", postService.getPostsByBlogId(blog.getId()));
//        return "blog";
//    }
//
//    @GetMapping("/user/blog")
//    public String getMyBlog(@CookieValue(value = "username", defaultValue = "") String username, Model model) {
//        if (!username.isEmpty()) {
//            Blog blog = blogService.getBlogByUsername(username);
//            model.addAttribute("blog", blog);
//            model.addAttribute("posts", postService.getPostsByBlogId(blog.getId()));
//            return "blog";
//        }
//        return "redirect:/loginform";
//    }
}
