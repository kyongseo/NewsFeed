package hello.blog.domain.controller;

import hello.blog.domain.domain.Blog;
import hello.blog.domain.dto.BlogDto;
import hello.blog.domain.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("blog", new Blog());
        return "blog/create";
    }

    @PostMapping("/create")
    public String createBlog(@ModelAttribute BlogDto blogDto,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        Optional<Blog> blog = blogService.findBlogByUserLoginId(userDetails.getUsername());
        if (blog.isEmpty()){
            Blog createBlog = blogService.createBlog(blogDto, userDetails.getUsername());
            return "redirect:/blogs/" + createBlog.getId();
        }
        redirectAttributes.addFlashAttribute("blogExistError","블로그가 이미 존재합니다.");
        return "redirect:/";
    }

    @GetMapping("{blogId}")
    public String getBlog(@PathVariable("blogId") Long blogId, Model model) {
        Blog blog = blogService.getBlogById(blogId);
        model.addAttribute("blog", blog);
        return "blog/view";
    }

    @GetMapping("/my")
    public String getMyBlog(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Optional<Blog> blog = blogService.findBlogByUserLoginId(userDetails.getUsername());
        if (blog.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "먼저 블로그를 생성하세요");
            return "redirect:/";
        }
        return "redirect:/api/blogs/" + blog.get().getId();
    }

    @GetMapping("/{blogId}/edit")
    public String editForm(@PathVariable("blogId") Long blogId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Blog blog = blogService.getBlogById(blogId);
        BlogDto blogDto = new BlogDto();
        blogDto.setTitle(blog.getTitle());
        model.addAttribute("blog", blogDto);
        model.addAttribute("blogId", blogId);
        return "blog/edit";
    }

    @PostMapping("{blogId}/edit")
    public String edit(@PathVariable("blogId") Long blogId,
                       @ModelAttribute BlogDto blogDto,
                       @AuthenticationPrincipal UserDetails userDetails) {
        blogService.updateBlog(blogDto, userDetails.getUsername());
        return "redirect:/api/blogs/" + blogId;
    }
}
