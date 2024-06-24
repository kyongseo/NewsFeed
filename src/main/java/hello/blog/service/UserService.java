package hello.blog.service;

import hello.blog.domain.Post;
import hello.blog.domain.Role;
import hello.blog.domain.RoleName;
import hello.blog.domain.User;
import hello.blog.repository.RoleRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // 회원가입
    public void registerUser(String username, String email, String password, String usernick) {
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserNick(usernick);
        Optional<Role> userRole = roleRepository.findByRoleName(RoleName.ROLE_USER);
        userRole.ifPresent(user.getRole()::add);
        userRepository.save(user);
    }

    public Optional<User> findByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    // 사용자 있는지 없는지 검증 로직 --
    public boolean validateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()){
            User user = userOptional.get();
            return user.getPassword().equals(password);
        }
        return false;
    }

    // 글등록할 때 사용자 이름을 기반으로 조회하기
    public Set<Post> getUserPosts(String username) {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            return userOptional.get().getPosts();
        }
        throw new RuntimeException("작성 권한이 없습니다.");
    }

    // 사용자 마이페이지 수정



}