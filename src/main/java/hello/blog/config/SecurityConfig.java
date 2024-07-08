package hello.blog.config;

import hello.blog.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/userregform", "/loginform", "/css/**", "/files/**", "/").permitAll() // 이 주소로 시작되면 인증 필요 없음
                        .requestMatchers("/posts/**","/{username}").permitAll() // 이 주소로 시작되면 인증 필요 없음
                        //.requestMatchers("/posts/create","{username}/edit").authenticated() // 이 주소로 시작되면 인증 필요
                        //.requestMatchers(HttpMethod.POST, "/posts/{postId}/edit", "/posts/{postId}/delete").authenticated()
                        //.requestMatchers(HttpMethod.GET, "/posts/{postId}/edit").authenticated()
                        .requestMatchers("/comments/post/**").authenticated()
                        .requestMatchers("/likes/post/**").authenticated()
                        .anyRequest().permitAll() // 그게 아닌 모든 주소는 인증 필요 없음
                )
                .formLogin(form -> form
                                .loginPage("/loginform") // 인증필요한 주소로 접속하면 이 주소로 이동시킴(GetMapping)
                                .loginProcessingUrl("/login") // 스프링 시큐리티가 로그인 자동 진행(PostMapping)
                                .defaultSuccessUrl("/") // 로그인이 정상적이면 "/" 로 이동
                                .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                )
                .sessionManagement(sessionManagement -> sessionManagement
                                .maximumSessions(1) // 동시 접속 허용 개수 : -1은 디폴트-무제한 , 1은 1개의 브라우저만 허용
                                .maxSessionsPreventsLogin(true) // 동시 로그인 차단 : 먼저 로그인된 아이를 차단할지, 나중에 로그인된 아이를 차단할지..
                        // 기본-false (먼저 로그인한 사용자를 차단), true(2번째 로그인 사용자를 차단)
                )

                .userDetailsService(customUserDetailsService)
                .csrf(csrf -> csrf.disable()); // csrf 토큰 비활성화 코드

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}