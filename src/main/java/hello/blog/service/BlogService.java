package hello.blog.service;

import hello.blog.domain.Blog;
import hello.blog.domain.User;
import hello.blog.exception.BlogNotFoundException;
import hello.blog.repository.BlogRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    // 블로그 생성
    public Blog createBlog(String title, User user) {
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setUser(user);
        return blogRepository.save(blog);
    }

    public Optional<Blog> findBlogByUser(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return user.flatMap(blogRepository::findBlogByUser);
    }

    public Blog getBlogById(Long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("해당 ID에 대한 블로그를 찾을 수 없습니다."));
    }
}