package hello.blog.security;

import hello.blog.domain.User;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

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
//        Optional<User> userOptional  = userRepository.findByUserName(username);
//        User user = userOptional .orElseThrow(() ->
//                new UsernameNotFoundException("해당 사용자가 존재하지 않습니다. : " + username));
//
//        if(user == null) {
//            throw new UsernameNotFoundException("사용자가 없습니다.");
//        }
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUserName())
//                .password(user.getPassword()) // 패스워드는 암호화된 값으로 설정해야 함
//                .authorities(Collections.emptyList()) // 사용자의 권한 정보 설정
//                .build();
//    }
//}