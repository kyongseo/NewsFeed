package hello.blog.domain.config;

import hello.blog.domain.domain.Role;
import hello.blog.domain.domain.RoleName;
import hello.blog.domain.domain.User;
import hello.blog.domain.repository.RoleRepository;
import hello.blog.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

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
                admin.setPassword("admin");  // 비밀번호는 실제로는 암호화하여 저장해야 함
                admin.getRole().add(adminRole);
                userRepository.save(admin);
            }
        };
    }
}