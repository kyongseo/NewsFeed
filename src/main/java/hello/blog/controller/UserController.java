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

//    @PostMapping("/login")
//    public ResponseEntity login(@RequestBody @Valid UserLoginDto userLoginDto,
//                                BindingResult bindingResult, HttpServletResponse response) {
//        // 입력된 데이터에 대한 유효성 검사
//        if (bindingResult.hasErrors()) {
//            return new ResponseEntity<>("Invalid input data", HttpStatus.BAD_REQUEST);
//        }
//
//        // 사용자 이름으로 사용자 조회
//        Optional<User> optionalUser = userService.findByUserName(userLoginDto.getUserName());
//        if (optionalUser.isEmpty()) {
//            return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
//        }
//
//        User user = optionalUser.get();
//
//        // 비밀번호 검증
//        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
//            return new ResponseEntity<>("Invalid password", HttpStatus.UNAUTHORIZED);
//        }
//
//        // 사용자의 역할(Role) 정보 추출
//        List<RoleName> roles = user.getRole().stream()
//                .map(Role::getRoleName)
//                .collect(Collectors.toList());
//
//        // 기존의 refreshToken 삭제
//        refreshTokenService.deleteRefreshToken(user.getUserId());
//
//        // JWT 토큰 생성
//        String accessToken = jwtTokenizer.createAccessToken(
//                user.getUserId(), user.getEmail(), user.getUserName(), roles);
//        String refreshToken = jwtTokenizer.createRefreshToken(
//                user.getUserId(), user.getEmail(), user.getUserName(), roles);
//
//        // 새로운 refreshToken 저장
//        RefreshToken refreshTokenEntity = new RefreshToken();
//        refreshTokenEntity.setValue(refreshToken);
//        refreshTokenEntity.setUserId(user.getUserId());
//        refreshTokenService.addRefreshToken(refreshTokenEntity);
//
//        // JWT 토큰을 쿠키로 설정하여 응답
//        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
//        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setPath("/");
//        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000)); // 초 단위
//
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000)); // 초 단위
//
//        response.addCookie(accessTokenCookie);
//        response.addCookie(refreshTokenCookie);
//
//        // 로그인 응답 DTO 생성
//        UserLoginResponseDto loginResponseDto = UserLoginResponseDto.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .userId(user.getUserId())
//                .userName(user.getUserName())
//                .build();
//
//        return ResponseEntity.ok(loginResponseDto);
//    }

//    @GetMapping("/api/logout")
//    public String logout(HttpServletResponse response) {
//        Cookie cookie = new Cookie("accessToken", null);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//        cookie.setMaxAge(0); // 쿠키 삭제
//
//        response.addCookie(cookie);
//        return "redirect:/"; // 로그아웃 후 API 페이지로 리다이렉트
//    }

//    @PostMapping("/loginform")
//    public ResponseEntity login(@RequestBody @Valid UserLoginDto userLoginDto,
//                                BindingResult bindingResult, HttpServletResponse response) {
//        if(bindingResult.hasErrors()) {
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
//
//        Optional<User> user = userService.findByUserName(userLoginDto.getUserName());
//        if(!passwordEncoder.matches(userLoginDto.getPassword(), user.get().getPassword())) {
//            return new ResponseEntity("비밀번호가 올바르지 않습니다.",HttpStatus.UNAUTHORIZED);
//
//        }
//
//        List<RoleName> roles = user.get().getRole().stream().map(Role::getRoleName).collect(Collectors.toList());
//
//        //토큰 발급
//        String accessToken = jwtTokenizer.createAccessToken(
//                user.get().getUserId(), user.get().getEmail(),user.get().getUserName(),roles);
//        String refreshToken = jwtTokenizer.createRefreshToken(
//                user.get().getUserId(), user.get().getEmail(), user.get().getUserName(), roles);
//
//        RefreshToken refreshTokenEntity = new RefreshToken();
//        refreshTokenEntity.setValue(refreshToken);
//        refreshTokenEntity.setUserId(user.get().getUserId());
//
//        refreshTokenService.addRefreshToken(refreshTokenEntity);
//
//
//        //응답으로 보낼 값들을 준비해요.
//        UserLoginResponseDto loginResponseDto = UserLoginResponseDto.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .userId(user.get().getUserId())
//                .userName(user.get().getUserName())
//                .build();
//
//        Cookie accessTokenCookie = new Cookie("accessToken",accessToken);
//        accessTokenCookie.setHttpOnly(true);  //보안 (쿠키값을 자바스크립트같은곳에서는 접근할수 없어요.)
//        accessTokenCookie.setPath("/");
//        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT/1000)); //30분 쿠키의 유지시간 단위는 초 ,  JWT의 시간단위는 밀리세컨드
//
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT/1000)); //7일
//
//        response.addCookie(accessTokenCookie);
//        response.addCookie(refreshTokenCookie);
//
//        return new ResponseEntity(loginResponseDto, HttpStatus.OK);
//
//    }

//
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
    public String showMyPage(@PathVariable("username") String username,
                             Model model,
                             Authentication authentication) {

        Optional<User> userOptional = userService.findByUserName(username); // 경로에서 전달된 사용자 아이디 조회

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            List<Post> allPosts = postService.getAllPosts();
            model.addAttribute("posts", allPosts);
            model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());

            // 상단에 로그인한 사용자의 프포필이 보이도록
            if (authentication != null) {
                String loggedInUsername = authentication.getName();
                model.addAttribute("username", loggedInUsername);

                Optional<User> loggedInUserOptional = userService.findByUserName(loggedInUsername);
                if (loggedInUserOptional.isPresent()) { // 로그인한 사용자의 프로필 사진이 보이도록
                    User loggedInUser = loggedInUserOptional.get();
                    model.addAttribute("loggedInProfileImage", "/files/" + loggedInUserOptional.get().getFilename());

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

        String username = authentication.getName(); // 현재 로그인한 사용자 아이디
        Optional<User> userOptional = userService.findByUserName(username); // 조회하고

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
