package hello.blog.controller;

import hello.blog.domain.*;
import hello.blog.dto.UserLoginDto;
import hello.blog.dto.UserLoginResponseDto;
import hello.blog.security.jwt.util.JwtTokenizer;
import hello.blog.service.FollowService;
import hello.blog.service.PostService;
import hello.blog.service.RefreshTokenService;
import hello.blog.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final FollowService followService;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;


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
            // id, email -> 중복 체크, password check -> 틀린지 같은지..
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

    // 특정 회원의 페이지 보여주기
    @GetMapping("/{username}")
    public String showMyPage(@PathVariable("username") String username,
                             Model model,
                             Authentication authentication) {

        Optional<User> userOptional = userService.findByUserName(username); // 경로에서 전달된 사용자 아이디 조회

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            List<Post> allPosts = postService.getAllPosts();
            model.addAttribute("posts", allPosts);
            // 로그인된 사용자가 아닌 사용자의 프로필 사진이 보여야 함
            model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());

            // 상단에는 로그인한 사용자의 프로필이 보여야 한다.
            if (authentication != null) {
                String loggedInUsername = authentication.getName();
                model.addAttribute("username", loggedInUsername);

                Optional<User> loggedInUserOptional = userService.findByUserName(loggedInUsername);
                if (loggedInUserOptional.isPresent()) { // 로그인한 사용자의 프로필 사진이 보이도록
                    User loggedInUser = loggedInUserOptional.get();
                    model.addAttribute("loggedInProfileImage", "/files/" + loggedInUser.getFilename());

                    // 팔로우 상태 확인 및 모델에 추가
                    boolean isFollowing = followService.isFollowing(loggedInUser, userOptional.get());
                    model.addAttribute("isFollowing", isFollowing);
                }
            } else {
                model.addAttribute("username", "");
                model.addAttribute("loggedInProfileImage", "");
                model.addAttribute("isFollowing", false);
            }
            return "/user/userPage";
        }
        return "redirect:/loginform";
    }

    //     로그인 성공 시 로그인한 유저의 마이페이지 보여주기
    @GetMapping("/mypage")
    public String showMyPage(Model model,
                             Authentication authentication) {

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

    // 마이페이지 수정
    @GetMapping("/mypage/edit")
    public String userEditForm(Model model,
                               Authentication authentication) {

        String username = authentication.getName();

        Optional<User> userOptional = userService.findByUserName(username);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());

            List<Post> allPosts = postService.getAllPosts();
            model.addAttribute("posts", allPosts);
            model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());
            model.addAttribute("username", username);

            // 로그인한 사용자 이름 추가
            if (authentication != null) {
                model.addAttribute("username", authentication.getName());
            } else {
                model.addAttribute("username", "");
            }
            return "/user/editPage";
        }
        return "redirect:/loginform";
    }

    // 마이페이지 수정 처리
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

    // 소개 페이지
    @GetMapping("/about/{username}")
    public String aboutUser(@PathVariable("username") String username,
                            Model model,
                            Authentication authentication) {

        // 현재 로그인한 사용자 아이디를 가져옴
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
