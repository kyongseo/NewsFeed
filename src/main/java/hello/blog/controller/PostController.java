package hello.blog.controller;

import hello.blog.domain.Comment;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.LikeRepository;
import hello.blog.service.CommentService;
import hello.blog.service.PostService;
//import hello.blog.service.TagService;
import hello.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeRepository likeRepository;

    /**
     * 게시글 등록
     * 쿠키가 아닌 인증된 사용자를 확인하는 코드 --> 사용자 이름(userName)을 이용하여 게시물 등록 작업 수행
     */
    @GetMapping("/create")
    public String showCreatePost(Model model) {
        model.addAttribute("post", new Post());
        return "/post/createPost";
    }

//    @PostMapping("/create")
//    public String createPost(@RequestParam("title") String title,
//                             @RequestParam("content") String content,
//                             @CookieValue(value = "username", defaultValue = "") String username) {
//
//        if (!username.isEmpty()) {
//            // 사용자 이름(userName)을 이용하여 게시물 등록 작업 수행
//            postService.createPost(username, title, content);
//        } else {
//            // 사용자 정보가 없는 경우 처리할 코드
//            return "redirect:/loginform";
//        }
//        return "redirect:/";
//    }

    @PostMapping("/create")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("image") MultipartFile file,
                             Authentication authentication) throws IOException {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Post post = postService.createPost(username, title, content, file);

            return "redirect:/";
        } else {
            return "redirect:/loginform";
        }
    }

    /**
     * 게시글 수정
     */
    @GetMapping("/{postId}/edit")
    public String showEditPost(@PathVariable("postId") Long postId,
                               Model model,
                               Authentication authentication) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (authentication == null || !authentication.isAuthenticated() || !postService.isPostOwner(postId, authentication.getName())) {
            return "redirect:/loginform";
        }

        model.addAttribute("post", post);
        return "/post/editPost";
    }

    @PostMapping("/{postId}/edit")
    public String editPost(@PathVariable("postId") Long postId,
                           @RequestParam("title") String title,
                           @RequestParam("content") String content,
                           @RequestParam("image") MultipartFile file,
                           Authentication authentication) throws IOException {
        if (authentication != null && authentication.isAuthenticated() && postService.isPostOwner(postId, authentication.getName())) {
            postService.updatePost(postId, title, content, file);
            return "redirect:/posts/" + postId;
        } else {
            return "redirect:/loginform";// 수정된 게시글로 리디렉션
        }
    }

    /**
     * 상세 페이지
     * 시큐리티에서 인증된 사용자를 확인
     */
//    @GetMapping("/{postId}")
//    public String getPostById(@PathVariable("postId") Long postId, Model model,
//                              @CookieValue(value = "username", defaultValue = "") String username) {
//
//        Post post = postService.getPostById(postId)
//                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
//
//        model.addAttribute("post", post);
//
//        if (!username.isEmpty()) {
//            Optional<User> userOptional = userService.findByUserName(username);
//            if (userOptional.isPresent()) {
//                User user = userOptional.get();
//                model.addAttribute("username", user.getUserName());
//                model.addAttribute("profileImage", "/files/" + user.getFilename());
//            }
//        } else {
//            model.addAttribute("username", "");
//        }
//
//        return "/post/viewPost";
//    }
    @GetMapping("/{postId}")
    public String getPostById(@PathVariable("postId") Long postId, Model model,
                              Authentication authentication) {

        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        List<Comment> comments = commentService.findByPostId(postId);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("postImage", "/files/" + post.getFilename());



        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                boolean liked = likeRepository.findByPostAndUser(post, user).isPresent();
                model.addAttribute("liked", liked);
                model.addAttribute("username", user.getUserName());
                model.addAttribute("profileImage", "/files/" + user.getFilename());
            }
        } else {
            model.addAttribute("username", "");
        }
        model.addAttribute("likeCount", likeRepository.countByPostId(postId));

        return "/post/viewPost";
    }

    /**
     * 게시글 삭제
     */
    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("postId") Long postId,
                             @RequestParam("_method") String method,
                             Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && postService.isPostOwner(postId, authentication.getName()) && "delete".equalsIgnoreCase(method)) {
            postService.deletePostById(postId);
        }
        return "redirect:/";
    }
}