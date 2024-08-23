package hello.blog.global.config.security;

import hello.blog.feature.domain.Role;
import hello.blog.feature.domain.RoleName;
import hello.blog.feature.domain.User;
import hello.blog.feature.repository.RoleRepository;
import hello.blog.feature.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 관리자 계정 생성
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    @Bean
    public CommandLineRunner initRolesAndAdminUser(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            // 역할이 없다면 역할 생성
            Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN).orElseGet(() -> {
                Role role = new Role();
                role.setRoleName(RoleName.ROLE_ADMIN);
                return roleRepository.save(role);
            });

            Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER).orElseGet(() -> {
                Role role = new Role();
                role.setRoleName(RoleName.ROLE_USER);
                return roleRepository.save(role);
            });

            // 관리자 계정이 없다면 생성
            if (!userRepository.findByUserName("admin").isPresent()) {
                User admin = new User();
                admin.setUserName("admin");
                admin.setUserNick("Admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.getRole().add(adminRole);
                userRepository.save(admin);
            }
        };
    }
}