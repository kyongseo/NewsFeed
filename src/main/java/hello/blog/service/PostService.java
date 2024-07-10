package hello.blog.service;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.PostRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 글 작성
    @Transactional
    public Post createPost(String username, String title, String content) {
        // 사용자 이름으로 사용자 조회
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Post 엔티티 생성 및 설정
            Post post = new Post();
            post.setTitle(title);
            post.setContent(content);
            post.setCreatedAt(LocalDateTime.now());
            post.setUser(user); // User 엔티티 설정

            return postRepository.save(post); // 저장 및 반환
        }
        throw new RuntimeException("작성 권한이 없습니다.");
    }


    // 글 전체 조회
    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts;
    }

    //게시물 검색
    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    // 게시글 수정
    public Post updatePost(Long postId, String title, String content) {

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setTitle(title);
            post.setContent(content);

            return postRepository.save(post);
        }
        throw new RuntimeException("게시글을 찾을 수 없습니다.");
    }

    // 게시물 삭제
    public void deletePostById(Long postId) {
        postRepository.deleteById(postId);
    }

    // 사용자별 게시글 조회
    public Set<Post> getPostsByUser(String username) {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            return userOptional.get().getPosts();
        }
        throw new RuntimeException("사용자를 찾을 수 없습니다.");
    }

    // 게시글 작성자 확인
    public boolean isPostOwner(Long postId, String username) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            return post.getUser().getUserName().equals(username);
        }
        return false;
    }

    // 최신 글 조회
    @Transactional(readOnly = true)
    public List<Post> getRecentPosts() {
        return postRepository.findTop10ByOrderByCreatedAtDesc(); // 최신 글 10개만 조회
    }

    // 게시물 검색
    public List<Post> searchPosts(String query) {
        return postRepository.findByTitleContainingOrContentContaining(query, query);
    }
}