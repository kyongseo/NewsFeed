package hello.blog.service;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.PostRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public List<Post> getAllPosts() { //게시물 전체보기
        List<Post> posts = postRepository.findAll();
        return posts;
    }

    public Optional<Post> getPostById(Long postId) { //게시물 검색
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

    //게시물 삭제
    public void deletePostById(Long postId) {
        postRepository.deleteById(postId);
    }
}

//@Service
//@RequiredArgsConstructor
//public class PostService {
//    private final PostRepository postRepository;
//    private final UserRepository userRepository;
//
//    @Transactional
//    public Post createPost(String title, String content, String username) {
//        //게시물 작성
//        Optional<User> userOptional = userRepository.findByUserName(username);
//        if (userOptional.isPresent()) {
//            Post post = new Post();
//            post.setTitle(title);
//            post.setContent(content);
//            post.setCreatedAt(LocalDateTime.now());
//            post.setUser(userOptional.get()); //작성자
//
//            return postRepository.save(post);
//        }
//        throw new RuntimeException("User not found");
//    }
//
//    public List<Post> getAllPosts() { //게시물 전체보기
//        List<Post> posts = postRepository.findAll();
//        return posts;
//    }
//
//    public Optional<Post> getPostById(Long postId) { //게시물 검색
//        return postRepository.findById(postId);
//    }
//
//    public Post updatePost(Long postId, String title, String content) {
//        //게시물 수정
//        Optional<Post> postOptional = postRepository.findById(postId);
//        if (postOptional.isPresent()) {
//            Post post = postOptional.get();
//            post.setTitle(title);
//            post.setContent(content);
//
//            return postRepository.save(post);
//        }
//        throw new RuntimeException("Post not found");
//    }
//
//    public void deletePostById(Long postId) { //게시물 삭제
//        postRepository.deleteById(postId);
//    }
//}