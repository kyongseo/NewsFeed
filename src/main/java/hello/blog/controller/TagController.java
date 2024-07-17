package hello.blog.controller;

import hello.blog.domain.Post;
import hello.blog.service.PostService;
import hello.blog.service.TagService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("tags/{tagName}")
    public String postsByTag(@PathVariable("tagName") String tagName, Model model) {
        List<Post> posts = tagService.findPostsByTag(tagName);
        model.addAttribute("posts", posts);
        model.addAttribute("tagName", tagName);
        return "post/tagList";
    }
}
