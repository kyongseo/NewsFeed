package hello.blog.service;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.LikeRepository;
import hello.blog.repository.PostRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 글 작성
    @Transactional
    public Post createPost(String username, String title, String content, MultipartFile file, boolean isDraft) throws IOException {
        // 사용자 이름으로 사용자 조회
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Post 엔티티 생성 및 설정
            Post post = new Post();
            post.setTitle(title);
            post.setContent(content);
            post.setCreatedAt(LocalDateTime.now());
            post.setUser(user);
            post.setDraft(isDraft); // 기본은 출간 상태(false)

            if (!file.isEmpty()) {
                uploadUserFile(post, file);
            }

            return postRepository.save(post); // 저장
        }
        throw new RuntimeException("작성 권한이 없습니다.");
    }

    @Transactional(readOnly = true)
    public List<Post> getDraftPostsByUser(String username) {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return postRepository.findByUserAndIsDraftTrue(user);
        }
        throw new RuntimeException("사용자를 찾을 수 없습니다.");
    }

    // 출간하기 기능 구현
    public void publishPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        post.setDraft(false); // 임시 저장 상태 해제
        postRepository.save(post);
    }

    private void uploadUserFile(Post post, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        post.setFilename(filename);
        post.setFilepath(filePath.toString());
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
    public Post updatePost(Long postId, String title, String content, MultipartFile file, boolean isDraft) throws IOException {

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setTitle(title);
            post.setContent(content);
            post.setDraft(isDraft); // 기본은 출간 상태(false)

            if (file != null && !file.isEmpty()) {
                uploadUserFile(post, file);
            } else if (file == null && post.getFilename() != null) {
                // 파일이 변경되지 않았을 때 기존 파일 유지
                post.setFilename(post.getFilename());
                post.setFilepath(post.getFilepath());
            }

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

    @Transactional(readOnly = true)
    public List<Post> getPostsOrderByLikes() {
        return postRepository.findAllOrderByLikesDesc();
    }


    /**
     * 관리자 - 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}