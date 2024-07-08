package hello.blog.controller;

import hello.blog.domain.Like;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.LikeRepository;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LikeController {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final UserService userService;

    @PostMapping("/posts/{postId}/like")
    public String like(@PathVariable("postId") Long postId,
                       Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<User> userOptional = userService.findByUserName(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<Post> postOptional = postService.getPostById(postId);

                if (postOptional.isPresent()) {
                    Post post = postOptional.get();
                    Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);

                    if (existingLike.isPresent()) {
                        // 이미 좋아요가 있는 경우, 좋아요 취소
                        likeRepository.delete(existingLike.get());
                    } else {
                        // 좋아요 추가
                        Like like = new Like();
                        like.setPost(post);
                        like.setUser(user);
                        likeRepository.save(like);
                    }
                }
            }
        }
        // 게시글 상세 페이지로 리다이렉트
        return "redirect:/posts/" + postId;
    }
}