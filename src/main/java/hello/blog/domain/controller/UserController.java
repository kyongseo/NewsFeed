package hello.blog.domain.controller;

import hello.blog.domain.domain.User;
import hello.blog.domain.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
                               @RequestParam("usernick") String usernick) {
        userService.registerUser(username, email, password, usernick);
        return "redirect:/loginform";
    }

    /**
     * 로그인
     */
    @GetMapping("/loginform")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/loginform")
    public String loginUser(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            HttpServletResponse response) {
        if (userService.validateUser(username, password)) {
            Cookie cookie = new Cookie("username", username);
            cookie.setMaxAge(60 * 60 * 24); //하루동안만
            response.addCookie(cookie);
            // 어드민 일때 아닐때 구분해서 리다이렉트 구현
            return "redirect:/" + username;
        }
        return "redirect:/loginform?error";
    }

    // 로그인 성공 시 username의 마이페이지 보여주기
    @GetMapping("/{username}")
    public String showMyPage(@PathVariable("username") String username, Model model) {
        Optional<User> userOptional = userService.findByUserName(username);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "mypage";
        }
        return "redirect:/loginform";
    }
}