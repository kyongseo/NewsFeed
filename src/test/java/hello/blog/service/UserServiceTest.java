package hello.blog.service;

import static org.mockito.Mockito.*;

import hello.blog.domain.Role;
import hello.blog.domain.RoleName;
import hello.blog.domain.User;
import hello.blog.repository.RoleRepository;
import hello.blog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void registerUserTest() throws IOException {
        // Given
        String username = "testUser";
        String email = "testUser@example.com";
        String password = "password";
        String passwordCheck = "password";
        String usernick = "testNick";
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test data".getBytes());
        String uploadDir = "uploads/";

        Role userRole = new Role();
        userRole.setRoleName(RoleName.ROLE_USER);

        when(roleRepository.findByRoleName(RoleName.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // When
        userService.registerUser(username, email, password, usernick, passwordCheck, file);

        // Then
        verify(userRepository, times(1)).save(any(User.class));

        // Verify file was uploaded
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        assert (Files.exists(filePath));

        // Clean up
        Files.deleteIfExists(filePath);
    }
}
