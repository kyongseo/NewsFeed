package hello.blog.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TagController {

    @GetMapping("/posts/tag/{tagname}")
    public String showTag(@PathVariable("tagname") String tagname) {

        return "/post/dreate";
    }
}
