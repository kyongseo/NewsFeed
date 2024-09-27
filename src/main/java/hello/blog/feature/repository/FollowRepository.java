package hello.blog.feature.repository;

import hello.blog.feature.domain.Follow;
import hello.blog.feature.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByUserAndFollowee(User user, User followee);

    List<Follow> findByUser(User user); // 내가

    List<Follow> findByFollowee(User followee); // 나를
}