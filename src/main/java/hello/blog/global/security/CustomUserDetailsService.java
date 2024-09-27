package hello.blog.global.security;

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

        Optional<User> user = userRepository.findByUserName(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("해당 사용자가 존재하지 않습니다. : " + username);
        }

        UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(username);
        userBuilder.password(user.get().getPassword());
        userBuilder.roles(user.get().getRole().stream()
                .map(role -> role.getRoleName().name().substring(5))
                .toArray(String[]::new));
        return userBuilder.build();
    }
}