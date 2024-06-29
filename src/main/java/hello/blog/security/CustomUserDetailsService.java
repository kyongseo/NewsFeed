package hello.blog.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * loadUserByUsername : 사용자의 세부 정보를 가져오는 메서드 --> 데이터베이스에서 사용자를 조회
 * loadUserByUsername 메서드는 주어진 사용자 이름(username)을 기반으로 사용자의 세부 정보를 검색하고 반환하는 역할
 */
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
//}
