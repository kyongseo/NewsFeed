package hello.blog.config;

//import hello.blog.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//    private final CustomUserDetailsService customUserDetailsService;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/user/**", "/mypage").hasAnyRole("USER", "ADMIN")
//                        .requestMatchers("/userregform","/userreg","/").permitAll()
//                        .anyRequest().authenticated()
//                )
////                .formLogin(Customizer.withDefaults())
//                .formLogin(form -> form
//                        .loginPage("/loginform")
//                        .loginProcessingUrl("/login")
//                        .defaultSuccessUrl("/welcome")
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
//                .csrf(csrf -> csrf.disable());
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//}