package hello.blog.controller;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.LikeRepository;
import hello.blog.service.FollowService;
import hello.blog.service.LikeService;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final PostService postService;
    private final LikeRepository likeRepository;
    private final FollowService followService;

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
        List<Post> searchResults = postService.searchPosts(query); // 제목, 내용 검색
        List<Post> searchUsersResults = postService.searchPostUser(query); // 작성자 검색

        // 두개를 따로따로 검색했는데 코드가 더러워서.... 하나로 합치기
        List<Post> comSearchResults  = new ArrayList<>(searchResults);
        comSearchResults .addAll(searchUsersResults);

        model.addAttribute("blogPosts", comSearchResults);

//        model.addAttribute("blogPosts", searchResults);
//        model.addAttribute("searchUsersResults", searchUsersResults);
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

    // 내가 팔로우한 상용자의 글만 보여주기 정렬 -- 피드 누르면 실행
    @GetMapping("/following")
    public String followingPosts(Model model,
                                 Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                model.addAttribute("nickname", user.getUserNick());
                model.addAttribute("username", user.getUserName());
                model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());

                List<User> followings = followService.getFollowings(user);
                List<Post> followingPosts = postService.getPostByUsers(followings);
                model.addAttribute("blogPosts", followingPosts);
            }
        } else {
            model.addAttribute("username", "");
            model.addAttribute("blogPosts", new ArrayList<>());
        }

        return "home";
    }
}