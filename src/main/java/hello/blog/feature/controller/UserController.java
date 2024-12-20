package hello.blog.feature.controller;

import hello.blog.feature.domain.Post;
import hello.blog.feature.domain.User;
import hello.blog.feature.service.FollowService;
import hello.blog.feature.service.PostService;
import hello.blog.feature.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final FollowService followService;

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
                               @RequestParam("password_check") String passwordCheck,
                               @RequestParam("usernick") String usernick,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(username, email, password, passwordCheck, usernick, file);
            redirectAttributes.addFlashAttribute("msg", "회원가입에 성공했습니다.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("msg", "회원가입에 실패했습니다: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/userregform";
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

    /**
     * 특정 회원의 페이지 보여주기
     */
    @GetMapping("/{username}")
    public String showMyPage(@PathVariable("username") String username,
                             Model model,
                             Authentication authentication) {

        userService.setAuthenticationAttributes(model, authentication);
        Optional<User> userOptional = userService.findByUserName(username);

        if (authentication != null && userOptional.isPresent()) {
            User loggedInUser = userService.findByUserName(authentication.getName()).orElse(null);
            if (loggedInUser != null) {
                boolean isFollowing = followService.isFollowing(loggedInUser, userOptional.get());
                model.addAttribute("isFollowing", isFollowing);
                List<Post> allPosts = postService.getAllPosts();
                model.addAttribute("posts", allPosts);
            }
        }
        return "/user/userPage";
    }

    /**
     * 마이페이지
     */
    @GetMapping("/mypage")
    public String showMyPage(Model model,
                             Authentication authentication) {

        userService.setAuthenticationAttributes(model, authentication);
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

    /**
     * 마이페이지 수정
     */
    @GetMapping("/mypage/edit")
    public String userEditForm(Model model,
                               Authentication authentication) {

        userService.setAuthenticationAttributes(model, authentication);

        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUserName(username);

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            List<Post> allPosts = postService.getAllPosts();
            model.addAttribute("posts", allPosts);
            model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());

            return "/user/editPage";
        }
        return "redirect:/loginform";
    }

    /**
     * 마이페이지 수정 처리
     */
    @PostMapping("/mypage/edit")
    public String editUser(Authentication authentication,
                           @RequestParam("email") String email,
                           @RequestParam("usernick") String usernick,
                           @RequestParam("file") MultipartFile file,
                           RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        try {
            userService.updateUser(username, email, usernick, file);
            redirectAttributes.addFlashAttribute("msg", "회원정보가 수정되었습니다.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("msg", "회원정보 수정에 실패했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/" + username;
    }

    /**
     * 사용자 탈퇴 처리
     */
    @PostMapping("/mypage/delete")
    public String deleteUser(Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        String username = authentication.getName();

        try {
            userService.deleteUser(username);
            redirectAttributes.addFlashAttribute("msg", "회원 탈퇴가 완료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원 탈퇴 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/trending";
    }

    /**
     * 소개 페이지
     */
    @GetMapping("/about/{username}")
    public String aboutUser(@PathVariable("username") String username,
                            Model model,
                            Authentication authentication) {

        String loggedInUsername = null;
        if (authentication != null && authentication.isAuthenticated()) {
            loggedInUsername = authentication.getName();
            model.addAttribute("username", loggedInUsername);

            // 상단에 로그인한 사용자의 프포필이 보이도록
            Optional<User> loggedInUserOptional = userService.findByUserName(loggedInUsername);
            if (loggedInUserOptional.isPresent()) {
                model.addAttribute("loggedInProfileImage", "/files/" + loggedInUserOptional.get().getFilename());
            }
        } else {
            model.addAttribute("username", "");
            model.addAttribute("loggedInProfileImage", "");
        }

        Optional<User> userOptional = userService.findByUserName(username); // 조회하고

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);

            List<Post> allPosts = postService.getAllPosts();
            model.addAttribute("posts", allPosts);
            model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());
            return "/user/aboutPage";
        }
        return "redirect:/loginform";
    }

    @PostMapping("/about/{username}")
    public String updateAbout(@PathVariable("username") String username,
                              @RequestParam("about") String about,
                              Authentication authentication,
                              Model model) {

        String loggedInUsername = null;
        if (authentication != null && authentication.isAuthenticated()) {
            loggedInUsername = authentication.getName();
        }
        Optional<User> userOptional = userService.findByUserName(loggedInUsername);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getUserName().equals(username) || loggedInUsername.equals("admin")) {
                user.setAbout(about);
                userService.saveUser(user);

                model.addAttribute("user", user);

                List<Post> allPosts = postService.getAllPosts();
                model.addAttribute("posts", allPosts);
                model.addAttribute("username", user.getUserName());
                model.addAttribute("profileImage", "/files/" + user.getFilename());

                return "/user/aboutPage";
            }
        }
        return "redirect:/loginform";
    }
}
