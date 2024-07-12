package hello.blog.config;

//import hello.blog.security.CustomOAuth2UserDetails;
import hello.blog.domain.RoleName;
import hello.blog.security.CustomUserDetails;
import hello.blog.security.CustomUserDetailsService;
import hello.blog.security.jwt.exception.CustomAuthenticationEntryPoint;
import hello.blog.security.jwt.filter.JwtAuthenticationFilter;
import hello.blog.security.jwt.util.JwtTokenizer;
//import hello.blog.security.oauth.CustomOAuth2AuthenticationSuccessHandler;
import hello.blog.service.JwtBlacklistService;
import hello.blog.service.RefreshTokenService;
import hello.blog.service.SocialUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    /**
     * security
     */
//    private final CustomUserDetailsService customUserDetailsService;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/userregform", "/loginform", "/css/**", "/files/**", "/").permitAll() // 이 주소로 시작되면 인증 필요 없음
//                        .requestMatchers("/admin/**", "/admin").hasRole("ADMIN")
//                        .requestMatchers("/posts/**", "/{username}", "/about/{username}").permitAll() // 이 주소로 시작되면 인증 필요 없음
//                        //.requestMatchers("/posts/create","{username}/edit").authenticated() // 이 주소로 시작되면 인증 필요
//                        //.requestMatchers(HttpMethod.POST, "/posts/{postId}/edit", "/posts/{postId}/delete").authenticated()
//                        //.requestMatchers(HttpMethod.GET, "/posts/{postId}/edit").authenticated()
//                        .requestMatchers("/comments/post/**").authenticated()
//                        .requestMatchers("/likes/post/**").authenticated()
//                        .anyRequest().permitAll() // 그게 아닌 모든 주소는 인증 필요 없음
//                )
//                .formLogin(form -> form
//                        .loginPage("/loginform") // 인증필요한 주소로 접속하면 이 주소로 이동시킴(GetMapping)
//                        .loginProcessingUrl("/login") // 스프링 시큐리티가 로그인 자동 진행(PostMapping)
//                        .defaultSuccessUrl("/") // 로그인이 정상적이면 "/" 로 이동
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/")
//                )
//                .sessionManagement(sessionManagement -> sessionManagement
//                                .maximumSessions(1) // 동시 접속 허용 개수 : -1은 디폴트-무제한 , 1은 1개의 브라우저만 허용
//                                .maxSessionsPreventsLogin(true) // 동시 로그인 차단 : 먼저 로그인된 아이를 차단할지, 나중에 로그인된 아이를 차단할지..
//                        // 기본-false (먼저 로그인한 사용자를 차단), true(2번째 로그인 사용자를 차단)
//                )
//
//                .userDetailsService(customUserDetailsService)
//                .csrf(csrf -> csrf.disable()); // csrf 토큰 비활성화 코드
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}



    /**
     * jwt
     */
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtBlacklistService jwtBlacklistService;
    private final RefreshTokenService refreshTokenService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 인가 규칙을 설정합니다.
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/api/login", "/api/**", "/loginform", "/css/**", "/files/**").permitAll()
                        .requestMatchers("/userregform", "/files/**", "/").permitAll()
                        .anyRequest().authenticated()
                )
                // UsernamePasswordAuthenticationFilter 앞에 JWT 인증 필터를 추가합니다.
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer, jwtBlacklistService, refreshTokenService), UsernamePasswordAuthenticationFilter.class)
               .formLogin(form -> form.disable())
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .loginProcessingUrl("/api/login")
//                        .failureUrl("/login?error=true")
//                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // CSRF 보호를 비활성화합니다.
                .csrf(csrf -> csrf.disable())
                // HTTP 기본 인증을 비활성화합니다.
                .httpBasic(httpBasic -> httpBasic.disable())
                // CORS (Cross-Origin Resource Sharing)를 구성합니다.
                .cors(cors -> cors.configurationSource(configurationSource()))
                // 사용자 정의 인증 진입 지점을 처리하기 위한 예외 처리를 구성합니다.
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint));

        return http.build();
    }

    /**
     * 모든 origin, header, method를 허용하는 CORS 구성 소스를 정의합니다.
     */
    @Bean
    public CorsConfigurationSource configurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*"); //모든 도메인 허용
        config.addAllowedHeader("*"); //모든 HTTP 메서드 허용
        config.addAllowedMethod("*");
        config.setAllowedMethods(List.of("GET", "POST", "DELETE")); // 명시적으로 GET, POST, DELETE 메서드 허용
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * BCryptPasswordEncoder 빈을 정의합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}