package hello.blog.service;

import hello.blog.domain.Like;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.LikeRepository;
import hello.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public void like(Post post, User user) {
        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
        }
    }

    // 좋아요 수에 따라 게시글 가져오기
    @Transactional(readOnly = true)
    public List<Post> getPostsSortedByLikes() {
        List<Post> posts = postRepository.findAll(); // 모든 게시글 가져오기

        // 게시글을 좋아요 수에 따라 정렬
        posts.sort(Comparator.comparingInt(post -> (int) -likeRepository.countByPostId(post.getId())));

        return posts;
    }
}