package hello.blog.service;

import hello.blog.domain.Post;
import hello.blog.domain.Role;
import hello.blog.domain.RoleName;
import hello.blog.domain.User;
import hello.blog.repository.RoleRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 회원가입
    public void registerUser(String username, String email, String password, String usernick, MultipartFile file) throws IOException {
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserNick(usernick);

        if (!file.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            user.setFilename(filename);
            user.setFilepath(filePath.toString());
        }

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
    public void updateUser(String username, String email, String password, String usernick, MultipartFile file) throws IOException {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail(email);
            user.setPassword(password);
            user.setUserNick(usernick);

            if (!file.isEmpty()) {
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                Files.copy(file.getInputStream(), filePath);
                user.setFilename(filename);
                user.setFilepath(filePath.toString());
            }
            userRepository.save(user);
        }
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

}