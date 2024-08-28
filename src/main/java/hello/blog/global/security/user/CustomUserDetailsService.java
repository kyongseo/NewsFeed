package hello.blog.global.security.user;

import hello.blog.feature.domain.User;
import hello.blog.feature.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByUserName(username); // 데이터베이스에서 사용자 이름을 기준으로 사용자를 조회
       // User referenceById = userRepository.getReferenceById(Long.valueOf(username));
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("해당 사용자가 존재하지 않습니다. : " + username);
        }

        UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(username);
        userBuilder.password(user.get().getPassword()); // 암호화된 비밀번호를 설정
        userBuilder.roles(user.get().getRole().stream()
                .map(role -> role.getRoleName().name().substring(5))
                .toArray(String[]::new));
        return userBuilder.build();
    }
}