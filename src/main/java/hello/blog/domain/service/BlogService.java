package hello.blog.domain.service;

import hello.blog.domain.domain.Blog;
import hello.blog.domain.domain.User;
import hello.blog.domain.dto.BlogDto;
import hello.blog.domain.repository.BlogRepository;
import hello.blog.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    // 블로그 생성
    public Blog createBlog(BlogDto blogDto, String username) {
        Optional<User> user = userRepository.findByUserName(username);
        Blog blog = new Blog();
        blog.setUser(user.get());
        blog.setTitle(blogDto.getTitle());
        return blogRepository.save(blog);
    }

    @Transactional(readOnly = true)
    public Optional<Blog> findBlogByUserLoginId(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return blogRepository.findBlogByUser(user.get());
    }

    public void updateBlog(BlogDto blogDto, String loginId) {
        Optional<Blog> optionalBlog = findBlogByUserLoginId(loginId);
        Blog blog = optionalBlog.get();
        blog.setTitle(blogDto.getTitle());
        blogRepository.save(blog);

    }

    public Blog getBlogById(Long blogId) {
        return blogRepository.findById(blogId).orElse(null);
    }

}
