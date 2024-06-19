package hello.blog.domain.service;

import hello.blog.domain.domain.Role;
import hello.blog.domain.domain.RoleName;
import hello.blog.domain.domain.User;
import hello.blog.domain.repository.RoleRepository;
import hello.blog.domain.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

//    @PostConstruct
//    public void init() {
//        //초기 관리자 계정 생성
//        if (!userRepository.findByUserName("admin").isPresent()) {
//            User adminUser = new User();
//            adminUser.setUserName("admin");
//            adminUser.setPassword("1111");
//            adminUser.setEmail("admin@gmail.com");
//            adminUser.setUserNick("관리자");
//            adminUser.setRegistrationDate(LocalDate.now());
//            adminUser.setProfileImage(null);
//            adminUser.addRole(roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
//                    .orElseThrow(() -> new RuntimeException("Role not found")));
//            userRepository.save(adminUser);
//        }
//    }

    public User registerUser(String username, String email, String password, String usernick) {
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserNick(usernick);
        user.setRegistrationDate(LocalDate.now());
        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.addRole(userRole);

        return userRepository.save(user);
    }

    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
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
}