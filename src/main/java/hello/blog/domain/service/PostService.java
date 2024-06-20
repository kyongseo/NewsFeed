package hello.blog.domain.service;

import hello.blog.domain.domain.Blog;
import hello.blog.domain.domain.Post;
import hello.blog.domain.domain.User;
import hello.blog.domain.repository.BlogRepository;
import hello.blog.domain.repository.PostRepository;
import hello.blog.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public Post createPost(String title, String content, String username) {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Blog> blogOptional = blogRepository.findBlogByUser(user);
            if (blogOptional.isPresent()) {
                Blog blog = blogOptional.get();
                Post post = new Post();
                post.setTitle(title);
                post.setContent(content);
                post.setDetailLink("/posts/" + post.getId());
                post.setBlog(blog);
                return postRepository.save(post);
            }
        }
        throw new RuntimeException("블로그를 찾을 수 없습니다.");
    }

    public List<Post> getPostsByBlogId(Long blogId) {
        return postRepository.findByBlogId(blogId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Blog> blogOptional = blogRepository.findBlogByUser(user);
            if (blogOptional.isPresent()) {
                Blog blog = blogOptional.get();
                return postRepository.findByBlogId(blog.getId());
            }
        }
        return Collections.emptyList();
    }
}
