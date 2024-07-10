package hello.blog.service;

import hello.blog.domain.Follow;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    public boolean toggleFollow(User loggedInUser, User followeeUser) {
        Optional<Follow> existingFollow = followRepository.findByUserAndFollowee(loggedInUser, followeeUser);

        if (existingFollow.isPresent()) {
            followRepository.delete(existingFollow.get());
            return false; // 언팔로우
        } else {
            Follow follow = new Follow();
            follow.setUser(loggedInUser);
            follow.setFollowee(followeeUser);
            followRepository.save(follow);
            return true; // 팔로우
        }
    }

    public boolean isFollowing(User loggedInUser, User followeeUser) {
        return followRepository.findByUserAndFollowee(loggedInUser, followeeUser).isPresent();
    }
}