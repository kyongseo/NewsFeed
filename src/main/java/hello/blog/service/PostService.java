package hello.blog.service;

import hello.blog.domain.Post;
import hello.blog.domain.Tag;
import hello.blog.domain.User;
import hello.blog.repository.PostRepository;
import hello.blog.repository.TagRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 글 작성
    @Transactional
    public Post createPost(String username, String title, String content, MultipartFile file, boolean isDraft, List<String> tagList) throws IOException {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Post post = new Post();
            post.setTitle(title);
            post.setContent(content);
            post.setCreatedAt(LocalDateTime.now());
            post.setUser(user);
            post.setDraft(isDraft); // 기본은 출간 상태(false)

            if (!file.isEmpty()) {
                uploadUserFile(post, file);
            }

            Set<Tag> tags = new HashSet<>();
            for (String tagName : tagList) {
                Tag tag = tagRepository.findByName(tagName).orElseGet(() -> {
                    Tag newTag = new Tag(tagName);
                    tagRepository.save(newTag); // 새로운 태그를 저장
                    return newTag;
                });
                tags.add(tag);
            }
            post.setTags(new ArrayList<>(tags));

            return postRepository.save(post);
        }
        throw new RuntimeException("작성 권한이 없습니다.");
    }

    // 임시저장
    @Transactional(readOnly = true)
    public List<Post> getDraftPostsByUser(String username) {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return postRepository.findByUserAndIsDraftTrue(user);
        }
        throw new RuntimeException("사용자를 찾을 수 없습니다.");
    }

    // 파일 생성 메서드
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
    @Transactional
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
    @Transactional
    public void deletePostById(Long postId) {
        postRepository.deleteById(postId);
    }

    // 게시글 작성자 확인
    public boolean isPostOwner(Long postId, String username) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            return post.getUser().getUserName().equals(username); // 게시글 작성자와 로그인한 유저가 같다면
        }
        return false;
    }

    // 조회수
    @Transactional
    public int updateView(Long id){
        return postRepository.updateView(id);
    }

    // 최신 글 정렬
    @Transactional(readOnly = true)
    public List<Post> getRecentPosts() {
        return postRepository.findTopByOrderByCreatedAtDesc(); // 최신 글 조회
    }

    // 게시물 검색
    public List<Post> searchPosts(String query, Pageable pageable) {
        return postRepository.findByTitleContainingOrContentContaining(query, query, pageable);
    }

    // 게시글 검색 -- 작성자
    public List<Post> searchPostUser(String query) {
        return postRepository.findByUserUserNameContaining(query);
    }

    // 좋아요 순 정렬
    @Transactional(readOnly = true)
    public List<Post> getPostsOrderByLikes() {
        return postRepository.findAllOrderByLikesDesc();
    }

    // 페이지 처리
    @Transactional
    public Page<Post> getPostPageList(Pageable pageable){
        return postRepository.findAll(pageable);
    }

    /**
     * 관리자 - 게시글 삭제
     */
    @Transactional
    public void deletePostAdmin(Long postId) {
        postRepository.deleteById(postId);
    }

    // 팔로우한 사람 목록
    public List<Post> getPostByUsers(List<User> users) {
        return postRepository.findByUserIn(users);
    }
}