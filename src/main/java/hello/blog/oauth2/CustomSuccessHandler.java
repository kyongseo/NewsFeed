package hello.blog.oauth2;

import hello.blog.domain.RefreshToken;
import hello.blog.oauth2.dto.CustomOAuth2User;
import hello.blog.security.jwt.util.JwtTokenizer;
import hello.blog.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static hello.blog.security.jwt.util.JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenizer jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

            //Long userId = customUserDetails.getUserId(); // kakao
            Long userId = customUserDetails.getUserIdAsLong();
            //Long userId = customUserDetails.getUserIdAsLong(); // ID를 Long으로 변환
            String username = customUserDetails.getUsername();
            String name = customUserDetails.getName();
            List<String> roles = customUserDetails.getRoles();

            log.info("Oauth2 로그인 성곻했습니다. ");
            log.info("jwt 토큰 생성 :: userId: {}, username: {}, name: {}, roles: {}", userId, username, name, roles);

            String accessToken = jwtUtil.createAccessToken(userId, username, name, roles);
            String refreshToken = jwtUtil.createRefreshToken(userId, username, name, roles);

            log.info("Access Token :: {}", accessToken);
            log.info("Refresh Token :: {}", refreshToken);

            // 쿠키에 토큰 저장
            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(Math.toIntExact(jwtUtil.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(Math.toIntExact(REFRESH_TOKEN_EXPIRE_COUNT / 1000));

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // 리프레시 토큰 DB 저장
            Date date = new Date(System.currentTimeMillis() + JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT);
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setValue(refreshToken);
            refreshTokenEntity.setUserId(userId);

            refreshTokenService.addRefreshToken(refreshTokenEntity);

            // super.onAuthenticationSuccess(request, response, authentication);

            response.sendRedirect("/api/users/info");

        } catch (Exception e) {
            log.error("Authentication success handling failed", e);
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during authentication");
            }
        }
    }
}