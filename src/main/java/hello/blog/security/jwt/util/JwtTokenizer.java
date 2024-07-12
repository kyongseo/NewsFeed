package hello.blog.security.jwt.util;

import hello.blog.domain.RoleName;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

//@Component
//@Slf4j
//@Getter
//public class JwtTokenizer {
//    private final byte[] accessSecret;
//    private final byte[] refreshSecret;
//
//    public static Long ACCESS_TOKEN_EXPIRE_COUNT = 15 * 1000L; // 30분
//    public static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7일
//
//    public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecret, @Value("${jwt.refreshKey}") String refreshSecret){
//        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
//        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
//    }
//
//    private String createToken(Long id, String email, String username, List<RoleName> roles, long expireCount, byte[] secret) {
//        Claims claims = Jwts.claims().setSubject(email);
//        claims.put("userId", id);
//        claims.put("username", username);
//        claims.put("roles", roles);
//
//        Date now = new Date();
//        Date expiration = new Date(now.getTime() + expireCount);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(expiration)
//                .signWith(SignatureAlgorithm.HS256, secret)
//                .compact();
//    }
//
//    public String createAccessToken(Long id, String email, String username, List<RoleName> roles) {
//        return createToken(id, email, username, roles, ACCESS_TOKEN_EXPIRE_COUNT, accessSecret);
//    }
//
//    public String createRefreshToken(Long id, String email, String username, List<RoleName> roles) {
//        return createToken(id, email, username, roles, REFRESH_TOKEN_EXPIRE_COUNT, refreshSecret);
//    }
//
//    public static Key getSigningKey(byte[] secretKey) {
//        return Keys.hmacShaKeyFor(secretKey);
//    }
//
//    public Long getUserIdFromToken(String token) {
//    Claims claims = parseAccessToken(token);
//    return claims.get("userId", Long.class);
//}
//
//    public Claims parseToken(String token, byte[] secretKey) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey(secretKey))
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public Claims parseAccessToken(String accessToken) {
//        return parseToken(accessToken, accessSecret);
//    }
//
//    public Claims parseRefreshToken(String refreshToken) {
//        return parseToken(refreshToken, refreshSecret);
//    }
//
//    // 토큰이 만료되었는지 확인하는 메서드
//    public boolean isTokenExpired(String token, byte[] secretKey) {
//        try {
//            Jwts.parserBuilder().setSigningKey(getSigningKey(secretKey)).build().parseClaimsJws(token).getBody();
//            return false;
//        } catch (io.jsonwebtoken.ExpiredJwtException e) {
//            return true;
//        }
//    }
//
//    // 액세스 토큰이 만료되었는지 확인
//    public boolean isAccessTokenExpired(String accessToken) {
//        return isTokenExpired(accessToken, accessSecret);
//    }
//
//    // 리프레시 토큰이 만료되었는지 확인
//    public boolean isRefreshTokenExpired(String refreshToken) {
//        return isTokenExpired(refreshToken, refreshSecret);
//    }
//
//    // 리프레시 토큰으로 새로운 액세스 토큰을 발급
//    public String refreshAccessToken(String refreshToken) {
//        Claims claims = parseRefreshToken(refreshToken);
//        Long userId = claims.get("userId", Long.class);
//        String email = claims.getSubject();
//        String username = claims.get("username", String.class);
//        List<RoleName> roles = (List<RoleName>) claims.get("roles");
//        return createAccessToken(userId, email, username, roles);
//    }
//}

@Component
@Slf4j
@Getter
public class JwtTokenizer {
    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    public static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30분
    public static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7일

    public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecret, @Value("${jwt.refreshKey}") String refreshSecret){
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
    }

    //token create
    private String createToken(Long id, String email, String username, List<RoleName> roles, long expireCount, byte[] secret) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("userId", id);
        claims.put("username", username);
        claims.put("roles", roles);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireCount);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, getSigningKey(secret))
                .compact();
    }

    //AccessToken생성
    public String createAccessToken(Long id, String email, String username, List<RoleName> roles) {
        return createToken(id, email, username, roles, ACCESS_TOKEN_EXPIRE_COUNT, accessSecret);
    }

    //RefreshToken생성
    public String createRefreshToken(Long id, String email, String username, List<RoleName> roles) {
        return createToken(id, email, username, roles, REFRESH_TOKEN_EXPIRE_COUNT, refreshSecret);
    }

    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }

    public Long getUserIdFromToken(String token) {
        // 3 중 추가
//        String[] tokenArr = token.split(" ");
//        if (tokenArr.length > 1) {
//            token = tokenArr[1];
//        }
        Claims claims = parseToken(token, accessSecret);
        return Long.valueOf((Integer) claims.get("userId"));
    }

    public Claims parseToken(String token, byte[] secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    // Token 유효성 검증
//    public boolean isTokenExpired(String token, byte[] secretKey) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey(secretKey))
//                    .build()
//                    .parseClaimsJws(token).getBody();
//            return false;
//        } catch (io.jsonwebtoken.ExpiredJwtException e) {
//            return true;
//        }
//    }

    public boolean isTokenExpired(String token, byte[] secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(secretKey))
                    .build()
                    .parseClaimsJws(token);
            return false; // 토큰이 유효할 때 false를 반환해야 함
        } catch (ExpiredJwtException e) {
            log.error("Token expired", e);
        } catch (Exception e) {
            log.error("Token invalid", e);
        }
        return true; // 토큰이 만료되었거나 유효하지 않을 때 true를 반환
    }

    public boolean isAccessTokenExpired(String accessToken) {
        return isTokenExpired(accessToken, accessSecret);
    }

    public boolean isRefreshTokenExpired(String refreshToken) {
        return isTokenExpired(refreshToken, refreshSecret);
    }

    public String refreshAccessToken(String refreshToken) {
        Claims claims = parseRefreshToken(refreshToken);
        Long userId = claims.get("userId", Long.class);
        String email = claims.getSubject();
        String username = claims.get("username", String.class);
        List<RoleName> roles = (List<RoleName>) claims.get("roles");
        return createAccessToken(userId, email, username, roles);
    }
}