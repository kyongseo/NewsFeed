package hello.blog.controller;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Set;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    /**
     * 회원가입
     */
    @GetMapping("/userregform")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/userregform")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               @RequestParam("usernick") String usernick,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(username, email, password, usernick, file);
            redirectAttributes.addAttribute("msg","회원가입에 성공했습니다.");
        } catch (IOException e) {
            redirectAttributes.addAttribute("msg","회원가입에 실패했습니다.");
            e.printStackTrace();
        }
        return "redirect:/loginform";
    }

    /**
     * 로그인
     */
    @GetMapping("/loginform")
    public String showLoginForm() {
        return "login";
    }

//    @PostMapping("/loginform")
//    public String loginUser(@RequestParam("username") String username,
//                            @RequestParam("password") String password,
//                            HttpServletResponse response) {
//        if (userService.validateUser(username, password)) {
//            Cookie cookie = new Cookie("username", username);
//            cookie.setMaxAge(60 * 60 * 24); //하루동안만
//            response.addCookie(cookie);
//            // 어드민 일 때와 아닐 때 구분해서 리다이렉트 구현
//            if ("admin".equals(username) && "admin".equals(password)) {
//                return "redirect:/admin/dashboard";
//            }
//            return "redirect:/" + username;
//        }
//        return "redirect:/loginform?error";
//    }

    // 특정 회원의 페이지 보여주기
    @GetMapping("/{username}")
    public String showMyPage(@PathVariable("username") String username, Model model) {
        Optional<User> userOptional = userService.findByUserName(username);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            List<Post> allPosts = postService.getAllPosts();
            model.addAttribute("posts", allPosts);
            model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());
            return "/user/mypage";
        }
        return "redirect:/loginform";
    }

    // 로그인 성공 시 로그인한 유저의 마이페이지 보여주기
    @GetMapping("/mypage")
    public String showMyPage(Model model, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUserName(username);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            List<Post> allPosts = postService.getAllPosts();
            model.addAttribute("posts", allPosts);
            model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());
            return "/user/mypage";
        }
        return "redirect:/loginform";
    }
}