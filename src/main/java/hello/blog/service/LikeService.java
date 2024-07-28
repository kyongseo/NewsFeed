package hello.blog.service;

import hello.blog.domain.Like;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

//    public void like(Post post, User user) {
//        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);
//        if (existingLike.isPresent()) {
//            likeRepository.delete(existingLike.get());
//        } else {
//            Like like = new Like();
//            like.setPost(post);
//            like.setUser(user);
//            likeRepository.save(like);
//        }
//    }

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