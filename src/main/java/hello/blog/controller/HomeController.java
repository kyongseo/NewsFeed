package hello.blog.controller;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.LikeRepository;
import hello.blog.service.LikeService;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final PostService postService;
    private final LikeRepository likeRepository;

    // 메인 홈 화면
    @GetMapping("/")
    public String showHomePage(Model model,
                               Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                model.addAttribute("nickname", user.getUserNick());
                model.addAttribute("username", user.getUserName());
                model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());
            }
        } else {
            model.addAttribute("username", "");
        }

        List<Post> blogPosts = postService.getAllPosts();
        model.addAttribute("blogPosts", blogPosts);
        return "home";
    }

    // 최신글 목록
    @GetMapping("/recent")
    public String recentPosts(Model model,
                              Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("nickname", user.getUserNick());
                model.addAttribute("username", user.getUserName());
                model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());
            }
        } else {
            model.addAttribute("username", "");
        }
        List<Post> latestPosts = postService.getRecentPosts();

        model.addAttribute("blogPosts", latestPosts);
        return "home";
    }

    // 검색어 찾기
    @GetMapping("/search")
    public String searchPosts(@RequestParam("query") String query,
                              Model model,
                              Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("nickname", user.getUserNick());
                model.addAttribute("username", user.getUserName());
                model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());
            }
        } else {
            model.addAttribute("username", "");
        }
        List<Post> searchResults = postService.searchPosts(query);
        model.addAttribute("blogPosts", searchResults);
        return "home";
    }

    // 좋아요 순 정렬 -- 트렌딩 클릭시
    @GetMapping("/trending")
    public String trendingPosts(Model model,
                                Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("nickname", user.getUserNick());
                model.addAttribute("username", user.getUserName());
                model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());
            }
        } else {
            model.addAttribute("username", "");
        }
        List<Post> trendingPosts = postService.getPostsOrderByLikes();
        model.addAttribute("blogPosts", trendingPosts);
        return "home";
    }
//    @GetMapping("/")
//    public String showHomePage(Model model,
//                               @CookieValue(value = "username", defaultValue = "") String username) {
//        if (!username.isEmpty()) {
//            Optional<User> userOptional = userService.findByUserName(username);
//            if (userOptional.isPresent()) {
//                User user = userOptional.get();
//                model.addAttribute("nickname", user.getUserNick());
//                model.addAttribute("username", user.getUserName());
//                model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());
//            }
//        } else {
//            model.addAttribute("username", "");
//        }
//        List<Post> blogPosts = postService.getAllPosts();
//        model.addAttribute("blogPosts", blogPosts);
//        return "home";
//    }

//    @GetMapping("/logout")
//    public String logoutUser (HttpServletResponse response){
//        Cookie cookie = new Cookie("username", null);
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);
//        return "redirect:/";
//    }
}