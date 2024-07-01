package hello.blog.security;

import hello.blog.domain.User;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * loadUserByUsername : 사용자의 세부 정보를 가져오는 메서드 --> 데이터베이스에서 사용자를 조회
 * loadUserByUsername 메서드는 주어진 사용자 이름(username)을 기반으로 사용자의 세부 정보를 검색하고 반환하는 역할
 */
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        Optional<User> userOptional = userRepository.findByUserName(username);
//        if (userOptional.isEmpty()) {
//            throw new UsernameNotFoundException("사용자가 없습니다.");
//        }
//        User user = userOptional.get();
//        Set<GrantedAuthority> grantedAuthorities = user.getRole().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
//                .collect(Collectors.toSet());
//
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUserName())
//                .password(user.getPassword())
//                .authorities(grantedAuthorities)
//                .build();
//    }
//}