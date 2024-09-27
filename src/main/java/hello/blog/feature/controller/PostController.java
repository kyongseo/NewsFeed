package hello.blog.feature.controller;

import hello.blog.feature.domain.Comment;
import hello.blog.feature.domain.Post;
import hello.blog.feature.domain.User;
import hello.blog.feature.repository.LikeRepository;
import hello.blog.feature.service.CommentService;
import hello.blog.feature.service.PostService;
import hello.blog.feature.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeRepository likeRepository;

    /**
     * 게시글 등록
     */
    @GetMapping("/create")
    public String showCreatePost(Model model) {
        model.addAttribute("post", new Post());
        return "/post/createPost";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("image") MultipartFile file,
                             @RequestParam("tags") String tags,
                             @RequestParam(value = "isDraft", defaultValue = "false") boolean isDraft,
                             Authentication authentication) throws IOException {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            // tag --> 띄어쓰기로 구분
            List<String> tagList = Arrays.asList(tags.split("\\s*,\\s*"));
            Post post = postService.createPost(username, title, content, file, isDraft, tagList);

            log.info("게시글 생성 Id : {}", post.getId());

            if (isDraft) {
                return "redirect:/posts/drafts";
            } else {
                return "redirect:/trending";
            }
        } else {
            return "redirect:/loginform";
        }
    }

    /**
     * 임시저장 목록 보여주기
     */
    @GetMapping("/drafts")
    public String getDraftPosts(Model model,
                                Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            List<Post> draftPosts = postService.getDraftPostsByUser(username);
            model.addAttribute("draftPosts", draftPosts);
            return "/post/draftPosts";
        }
        return "redirect:/loginform";
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
                           @RequestParam("tags") String tags,
                           @RequestParam(value = "isDraft", defaultValue = "false") boolean isDraft,
                           Authentication authentication) throws IOException {

        if (authentication != null && authentication.isAuthenticated() && postService.isPostOwner(postId, authentication.getName())) {
            List<String> tagList = Arrays.asList(tags.split("\\s*,\\s*"));
            postService.updatePost(postId, title, content, file, isDraft, tagList);

            if (isDraft) {
                return "redirect:/posts/drafts";
            } else {
                return "redirect:/trending";
            }
        } else {
            return "redirect:/loginform";
        }
    }

    /**
     * 상세 페이지
     */
    @GetMapping("/{postId}")
    public String getPostById(@PathVariable("postId") Long postId,
                              Model model, Authentication authentication) {

        postService.updateView(postId);

        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        List<Comment> comments = commentService.findByPostId(postId);

        model.addAttribute("viewCount", post.getView());
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

        if (authentication != null && authentication.isAuthenticated()
                                    && postService.isPostOwner(postId, authentication.getName())
                                    && "delete".equalsIgnoreCase(method)) {

            postService.deletePostById(postId);
        }
        return "redirect:/trending";
    }
}