package hello.blog.feature.service;

import hello.blog.feature.domain.Role;
import hello.blog.feature.domain.RoleName;
import hello.blog.feature.domain.User;
import hello.blog.feature.repository.RoleRepository;
import hello.blog.feature.repository.UserRepository;
import hello.blog.global.config.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // 파일 업로드 디렉토리 경로
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 사용자, 관리자 - 회원가입
     */
    @Transactional
    public void registerUser(String username, String email, String password, String passwordCheck, String usernick, MultipartFile file) throws IOException {
        if (!password.equals(passwordCheck)) {
            throw new IllegalArgumentException("비밀번호가 다릅니다。");
        }
        if (userRepository.existsByUserName(username)) {
            throw new IllegalArgumentException("아이디가 존재합니다.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이메일이 존재합니다.");
        }

        Role role;

        // admin 사용자의 비밀번호를 암호화 안해서 암호화하기
        if (username.equals("admin")) {
            Optional<User> adminUser = userRepository.findByUserName("admin");
            if (adminUser.isPresent()) {
                User existingAdmin = adminUser.get();
                existingAdmin.setPassword(passwordEncoder.encode(password));
                uploadUserFile(existingAdmin, file);
                userRepository.save(existingAdmin);
                return;
            }
            role = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new UserNotFoundException("Admin 역할이 없습니다."));
        } else {
            role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new UserNotFoundException("User 역할이 없습니다."));
        }

        // 관리자가 아니면 user 역할의 새로운 사용자 객체 생성
        User user = new User();
        user.setRole(Collections.singleton(role));  // singleton -- 단일 역할 설정
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserNick(usernick);

        // 파일 업로드 처리
        if (!file.isEmpty()) {
            // 회원가입할 때 파일을 추가했으면 업로드된 파일 사용
            uploadUserFile(user, file);
        } else {
            // 추가하지않은 채 회원가입했다면 기본 이미지 사용
            String defaultFilename = "user.png";
            String defaultFilepath = uploadDir + defaultFilename;
            user.setFilename(defaultFilename);
            user.setFilepath(defaultFilepath);
        }

        userRepository.save(user);
    }

    /**
     * 사용자 - 회원 탈퇴
     */
    @Transactional
    public void deleteUser(String username) {
        // 사용자 삭제
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
        } else {
            throw new IllegalArgumentException("삭제할 사용자가 존재하지 않습니다.");
        }
    }

    /**
     * 사용자 - 한줄 소개 업로드
     */
    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Oauth2
     */
    public User saveOauthUser(String username, String email, String socialId, String provider, PasswordEncoder passwordEncoder){
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setSocialId(socialId);
        user.setProvider(provider);
        user.setPassword(passwordEncoder.encode(""));
        return userRepository.save(user);
    }

    /**
     * 사용자 - 아이디 가져오기
     * 로그인한 유저인지 확인
     */
    @Transactional
    public Optional<User> findByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    /**
     * 사용자 - 마이페이지 수정
     */
    @Transactional
    public void updateUser(String username, String email, String usernick, MultipartFile file) throws IOException {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail(email);
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

    /**
     * 관리자 - 회원가입한 전체 사용자 조회
     */
    @Transactional
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 관리자 - 회원 강제 삭제
     */
    @Transactional
    public void deleteUserAdmin(Long userId) {
        userRepository.deleteById(userId);
    }

    // 회원가입 시 파일 업로드 처리 메서드
    private void uploadUserFile(User user, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        user.setFilename(filename);
        user.setFilepath(filePath.toString());
    }

    /**
     * jwt 토큰 사용시 메서드
     */
    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }
}