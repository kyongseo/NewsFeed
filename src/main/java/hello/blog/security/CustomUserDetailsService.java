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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * loadUserByUsername : 사용자의 세부 정보를 가져오는 메서드 --> 데이터베이스에서 사용자를 조회
 * loadUserByUsername 메서드는 주어진 사용자 이름(username)을 기반으로 로그인 아이디 확인 -- 사용자의 세부 정보를 검색하고 반환하는 역할
 * 시큐리티 설정에서 loginProcessingUrl("/login")
 * login 요청이 오면 자동으로 UserDetailService 타입으로 Ioc 되어있는 loadUserByUserName 함수가 실행
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//        Optional<User> userOptional  = userRepository.findByUserName(username); // 데이터베이스에서 사용자 이름을 기준으로 사용자를 조회
//        User user = userOptional .orElseThrow(() ->
//                new UsernameNotFoundException("해당 사용자가 존재하지 않습니다. : " + username));
//
//        // UserBuilder를 사용하여 UserDetails 객체를 생성 --> 사용자 정보를 쉽게 생성하고 구성
//        // withUsername(username)를 호출하여 사용자 이름을 설정
//        org.springframework.security.core.userdetails.User.UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(username);
//        userBuilder.password(user.getPassword()); // 암호화된 비밀번호를 설정
//        userBuilder.roles(user.getRole().stream().map(role -> role.getRoleName()).toArray(String[]::new));

        Optional<User> userOptional = userRepository.findByUserName(username); // 데이터베이스에서 사용자 이름을 기준으로 사용자를 조회
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("해당 사용자가 존재하지 않습니다. : " + username);
        }

        User user = userOptional.get();

        // 현재 사용자의 역할을 설정하는 부분 확인
        Set<GrantedAuthority> authorities = user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name())) // role이 enum 타입이라소 문자열로 변환하여 사용
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
    }
}