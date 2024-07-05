package hello.blog.service;

import hello.blog.config.exception.UserNotFoundException;
import hello.blog.domain.Post;
import hello.blog.domain.Role;
import hello.blog.domain.RoleName;
import hello.blog.domain.User;
import hello.blog.repository.RoleRepository;
import hello.blog.repository.UserRepository;
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

    // 회원가입
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

        // 새로운 사용자 객체 생성
        User user = new User();
        user.setRole(Collections.singleton(role));  // singleton -- 단일 역할 설정
        user.setUserName(username);  // 사용자 이름 설정
        user.setEmail(email);  // 이메일 설정
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화 및 설정
        user.setUserNick(usernick); // 사용자 닉네임 설정

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

        // 사용자 저장
        userRepository.save(user);
    }

//        Optional<Role> userRole = roleRepository.findByRoleName(RoleName.ROLE_USER); // ROLE_USER 역할 조회
//
//        User user = new User();
////        user.setRole(new HashSet<>());
////        Set<Role> roles = new HashSet<>();
////        roles.add(userRole.get());
//        user.setRole(Collections.singleton(userRole.get())); // 사용자 객체에 역할 설정 -- 회원가입을 하는 모든 사용자는 기본적으로 다 단일 역할 ROLE_USER 부여
//        user.setUserName(username);
//        user.setEmail(email);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setUserNick(usernick);
//
//        if (!file.isEmpty()) {
//            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//
//            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//            Path filePath = uploadPath.resolve(filename);
//            Files.copy(file.getInputStream(), filePath);
//            user.setFilename(filename);
//            user.setFilepath(filePath.toString());
//        }
////       else {
////                // 기본 이미지 설정
////                File defaultUploadFile = new UploadFile("user.png", "user.png");
////                ImgFile defaultImgFile = new ImgFile();
////                defaultImgFile.setAttachFile(defaultUploadFile);
////                imgFileRepository.save(defaultImgFile);
////                blog.setProfileImg(defaultImgFile);
////        }
//
//        if (userRole.isPresent()) { // 역할이 존재하면
//            user.setRole(Collections.singleton(userRole.get()));
//        }
//        userRepository.save(user);
//    }

    @Transactional
    public Optional<User> findByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    // 사용자 있는지 없는지 검증 로직 --
//    public boolean validateUser(String username, String password) {
//        Optional<User> userOptional = userRepository.findByUserName(username);
//        if (userOptional.isPresent()){
//            User user = userOptional.get();
////            return user.getPassword().equals(password);
//            return passwordEncoder.matches(password, user.getPassword());
//        }
//        return false;
//    }

    // 글 등록할 때 사용자 이름을 기반으로 조회하기
    public Set<Post> getUserPosts(String username) {
        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isPresent()) {
            return userOptional.get().getPosts();
        }
        throw new RuntimeException("작성 권한이 없습니다.");
    }

    // 사용자 마이페이지 수정
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

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // 파일 업로드 처리 메서드
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
}