package hello.blog.config;

import hello.blog.security.CustomUserDetailsService;
import hello.blog.security.jwt.exception.CustomAuthenticationEntryPoint;
import hello.blog.security.jwt.filter.JwtAuthenticationFilter;
import hello.blog.security.jwt.util.JwtTokenizer;
//import hello.blog.security.oauth.CustomOAuth2AuthenticationSuccessHandler;
//import hello.blog.security.oauth.CustomOAuth2AuthenticationSuccessHandler;
import hello.blog.service.JwtBlacklistService;
import hello.blog.service.RefreshTokenService;
import hello.blog.service.SocialUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    /**
     * jwt
     */
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtBlacklistService jwtBlacklistService;
    private final RefreshTokenService refreshTokenService;
    //private final CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
    private final SocialUserService socialUserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/userregform", "/loginform", "/login", "/css/**", "/js/**", "/files/**", "/", "/api/login", "/api/**").permitAll() // 이 주소로 시작되면 인증 필요 없음
                        .requestMatchers("/posts/**", "/{username}", "/about/{username}", "/ws","/chat").permitAll()
                //        .requestMatchers("/oauth2/**", "/login/oauth2/code/github","/registerSocialUser","/saveSocialUser").permitAll()
                        .requestMatchers("/admin/**", "/admin").hasRole("ADMIN")
                        .requestMatchers("/comments/post/**", "/likes/post/**").authenticated()
                        .anyRequest().authenticated()
                )
                // UsernamePasswordAuthenticationFilter 앞에 JWT 인증 필터를 추가합니다.
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer, jwtBlacklistService, refreshTokenService), UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
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
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/loginform")
//                        .failureUrl("/trending")
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(this.oauth2UserService())
//                        )
//                        .successHandler(customOAuth2AuthenticationSuccessHandler)
//                );

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

    /**
     *
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return oauth2UserRequest -> {
            OAuth2User oauth2User = delegate.loadUser(oauth2UserRequest);
            // 여기에 Github 유저 정보를 처리하는 로직을 추가할 수 있습니다.
            // 예: DB에 사용자 정보 저장, 권한 부여 등

            String token = oauth2UserRequest.getAccessToken().getTokenValue();


            // Save or update the user in the database
            String provider = oauth2UserRequest.getClientRegistration().getRegistrationId();
            String socialId = String.valueOf(oauth2User.getAttributes().get("id"));
            String username = (String) oauth2User.getAttributes().get("login");
            String email = (String) oauth2User.getAttributes().get("email");
            String avatarUrl = (String) oauth2User.getAttributes().get("avatar_url");

            socialUserService.saveOrUpdateUser(socialId, provider, username, email, avatarUrl);

            return oauth2User;
        };
    }
}

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