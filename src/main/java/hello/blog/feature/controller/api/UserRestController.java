package hello.blog.feature.controller.api;

import hello.blog.feature.dto.UserLoginDto;
import hello.blog.feature.dto.UserLoginResponseDto;
import hello.blog.feature.domain.*;
import hello.blog.global.jwt.util.JwtTokenizer;
import hello.blog.feature.service.JwtBlacklistService;
import hello.blog.feature.service.RefreshTokenService;
import hello.blog.feature.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final JwtBlacklistService jwtBlackListService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid UserLoginDto userLoginDto,
                                BindingResult bindingResult, HttpServletResponse response) {

        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userService.findByUserName(userLoginDto.getUsername());

        if(!passwordEncoder.matches(userLoginDto.getPassword(), user.get().getPassword())) {

            return new ResponseEntity("비밀번호가 올바르지 않습니다.",HttpStatus.UNAUTHORIZED);
        }

        List<RoleName> roles = user.get().getRole().stream().map(Role::getRoleName).collect(Collectors.toList());

        refreshTokenService.deleteRefreshToken(user.get().getUserId());

        String accessToken = jwtTokenizer.createAccessToken(
                user.get().getUserId(), user.get().getEmail(), user.get().getUserName(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(
                user.get().getUserId(), user.get().getEmail(), user.get().getUserName(), roles);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setUserId(user.get().getUserId());

        refreshTokenService.addRefreshToken(refreshTokenEntity);

        UserLoginResponseDto loginResponseDto = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.get().getUserId())
                .username(user.get().getUserName())
                .build();

        Cookie accessTokenCookie = new Cookie("accessToken",accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT/1000)); //30분 쿠키의 유지시간 단위는 초 ,  JWT의 시간단위는 밀리세컨드

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT/1000)); //7일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity(loginResponseDto, HttpStatus.OK);
    }

    @GetMapping("/authtest")
    public ResponseEntity<String> authTest(){
        return ResponseEntity.ok("authTest");
    }

    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response){

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if("refreshToken".equals(cookie.getName())){
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if(refreshToken == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
        Long userId = Long.valueOf ((Integer)claims.get("userId"));

        User user = userService.getUser(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾지 못했습니다."));

        List roles = (List)claims.get("roles");

        String accessToken = jwtTokenizer.createAccessToken(userId, user.getEmail(), user.getUserName(),  roles);

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact( JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

        response.addCookie(accessTokenCookie);

        UserLoginResponseDto responseDto = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .username(user.getUserName())
                .build();

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }

    @GetMapping("/logout")
    public void logout(@CookieValue(name = "accessToken", required = false) String accessToken,
                       @CookieValue(name = "refreshToken", required = false) String refreshToken,
                       HttpServletResponse response) {

        if (accessToken == null) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Access token not found in cookies.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        String jwt = accessToken;
        System.out.println("accessToken: " + jwt);

        Date expirationTime = Jwts.parser()
                .setSigningKey(jwtTokenizer.getAccessSecret())
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration();

        JwtBlacklist blacklist = new JwtBlacklist(jwt, expirationTime);
        jwtBlackListService.save(blacklist);

        SecurityContextHolder.clearContext();

        // accessToken 쿠키 삭제
        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        // refreshToken 쿠키 삭제
        Cookie refresCcookie = new Cookie("refreshToken", null);
        refresCcookie.setPath("/");
        refresCcookie.setMaxAge(0);
        response.addCookie(refresCcookie);

        refreshTokenService.deleteRefreshToken(refreshToken);

        try {
            response.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}