package hello.blog.service;

import hello.blog.domain.Follow;
import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    // 팔로잉 중인 사용자 목록 조회
    public List<User> getFollowings(User user) {
        List<Follow> followings = followRepository.findByUser(user);
        // followings.forEach(f -> System.out.println("Following: " + f.getFollowee().getUserName()));
        return followings.stream()
                .map(Follow::getFollowee)
                .collect(Collectors.toList());
    }

    // 팔로워인 사용자 목록 조회
    public List<User> getFollowers(User user) {
        List<Follow> followers = followRepository.findByFollowee(user);
        return followers.stream()
                .map(Follow::getUser)
                .collect(Collectors.toList());
    }

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

    // 팔로우 상태 호거인
    public boolean isFollowing(User loggedInUser, User followeeUser) {
        return followRepository.findByUserAndFollowee(loggedInUser, followeeUser).isPresent();
    }
}