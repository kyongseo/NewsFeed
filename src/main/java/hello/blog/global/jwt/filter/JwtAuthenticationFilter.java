package hello.blog.global.jwt.filter;

import hello.blog.global.security.CustomUserDetails;
import hello.blog.global.jwt.exception.JwtExceptionCode;
import hello.blog.global.jwt.token.JwtAuthenticationToken;
import hello.blog.global.jwt.util.JwtTokenizer;
import hello.blog.feature.service.JwtBlacklistService;
import hello.blog.feature.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;
    private final JwtBlacklistService jwtBlacklistService;
    private final RefreshTokenService refreshTokenService;

    private static final List<String> PERMIT_ALL_PATHS = List.of(
            "/userregform", "/loginform", "/login", "/api/login",
            "/css/.*", "/js/.*", "/files/.*",
            "/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();

        // 인증이 필요 없는 경로인지 확인
        if (isPermitAllPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);

        if (!StringUtils.hasText(token)) {
            handleMissingToken(request, response);
        } else {
            handleTokenValidation(request, response, token);
        }

        filterChain.doFilter(request, response);
    }

    // 요청 경로가 인증 없이 접근 가능한지 확인하는 메서드
    private boolean isPermitAllPath(String requestPath) {
        return PERMIT_ALL_PATHS.stream().anyMatch(requestPath::matches);
    }

    // Access Token이 없는 경우 처리하는 메서드
    private void handleMissingToken(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String refreshToken = getRefreshToken(request);
        if (StringUtils.hasText(refreshToken)) {
            try {
                if (refreshTokenService.isRefreshTokenValid(refreshToken)) {
                    if (!jwtTokenizer.isRefreshTokenExpired(refreshToken)) {

                        String newAccessToken = jwtTokenizer.refreshAccessToken(refreshToken);
                        setAccessTokenCookie(response, newAccessToken);
                        getAuthentication(newAccessToken);
                    } else {
                        handleException(request, JwtExceptionCode.EXPIRED_TOKEN, "Refresh token expired");
                    }
                } else {
                    handleException(request, JwtExceptionCode.NOT_FOUND_TOKEN, "Refresh token not found in database");
                }
            } catch (ExpiredJwtException e) {
                handleException(request, JwtExceptionCode.EXPIRED_TOKEN, "Expired refresh token", e);
            }
        } else {
            handleException(request, JwtExceptionCode.NOT_FOUND_TOKEN, "Token not found in request");
        }
    }

    // Access Token이 있는 경우 처리하는 메서드
    private void handleTokenValidation(HttpServletRequest request, HttpServletResponse response, String token) throws ServletException, IOException {
        try {
            if (jwtBlacklistService.isTokenBlacklisted(token)) {
                handleException(request, JwtExceptionCode.BLACKLISTED_TOKEN, "Token is blacklisted: " + token);
            } else {
                getAuthentication(token);
            }
        } catch (ExpiredJwtException e) {
            handleExpiredAccessToken(request, response, token, e);
        } catch (UnsupportedJwtException e) {
            handleException(request, JwtExceptionCode.UNSUPPORTED_TOKEN, "Unsupported token: " + token, e);
        } catch (MalformedJwtException e) {
            handleException(request, JwtExceptionCode.INVALID_TOKEN, "Invalid token: " + token, e);
        } catch (IllegalArgumentException e) {
            handleException(request, JwtExceptionCode.NOT_FOUND_TOKEN, "Token not found: " + token, e);
        } catch (Exception e) {
            handleException(request, JwtExceptionCode.UNKNOWN_ERROR, "JWT filter internal error: " + token, e);
        }
    }

    // Access Token이 만료된 경우 처리하는 메서드
    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response, String token, ExpiredJwtException e) throws ServletException, IOException {
        log.warn("Access token expired: {}", token);
        String refreshToken = getRefreshToken(request);
        if (StringUtils.hasText(refreshToken) && !jwtTokenizer.isRefreshTokenExpired(refreshToken)) {
            String newAccessToken = jwtTokenizer.refreshAccessToken(refreshToken);
            setAccessTokenCookie(response, newAccessToken);
            getAuthentication(newAccessToken);
        } else {
            handleException(request, JwtExceptionCode.EXPIRED_TOKEN, "Expired Token : " + token, e);
        }
    }

    // 쿠키에서 Access Token을 추출하는 메서드
    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 쿠키에서 Refresh Token을 추출하는 메서드
    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 새로운 Access Token을 쿠키에 설정하는 메서드
    private void setAccessTokenCookie(HttpServletResponse response, String newAccessToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
        accessTokenCookie.setHttpOnly(true); // XSS 보호를 위해 HttpOnly 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
        response.addCookie(accessTokenCookie);
    }

    // Access Token을 사용하여 인증 정보 설정하는 메서드
    private void getAuthentication(String token) {
        Claims claims = jwtTokenizer.parseAccessToken(token);
        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        CustomUserDetails userDetails = new CustomUserDetails(username, "", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        Authentication authentication = new JwtAuthenticationToken(authorities, userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    // JWT Claims에서 권한 정보를 추출하는 메서드
    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> roles = (List<String>) claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(() -> role);
        }
        return authorities;
    }

    // 예외 처리 메서드
    private void handleException(HttpServletRequest request, JwtExceptionCode exceptionCode, String logMessage) {
        handleException(request, exceptionCode, logMessage, null);
    }

    private void handleException(HttpServletRequest request, JwtExceptionCode exceptionCode, String logMessage, Exception e) {
        request.setAttribute("exception", exceptionCode.getCode());
        log.error(logMessage, e);
        throw new BadCredentialsException(logMessage, e);
    }
}