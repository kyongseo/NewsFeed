package hello.blog.feature.service;

import hello.blog.feature.domain.Like;
import hello.blog.feature.domain.Post;
import hello.blog.feature.domain.User;
import hello.blog.feature.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    public boolean like(Post post, User user) {
        Optional<Like> likeOptional = likeRepository.findByPostAndUser(post, user);
        if (likeOptional.isPresent()) {
            likeRepository.delete(likeOptional.get());
            return false;
        } else {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            return true;
        }
    }

    public Long countByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }
}