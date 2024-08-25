package hello.blog.feature.service;

import hello.blog.feature.domain.JwtBlacklist;
import hello.blog.feature.repository.JwtBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final JwtBlacklistRepository jwtBlacklistRepository;

    public void save(JwtBlacklist blacklist) {
        jwtBlacklistRepository.save(blacklist);
    }

    public void addToBlacklist(String token, Date expirationDate) {
        JwtBlacklist blacklist = new JwtBlacklist(token, expirationDate);
        jwtBlacklistRepository.save(blacklist);
    }

    public boolean isTokenBlacklisted(String token) {
        return jwtBlacklistRepository.existsByToken(token);
    }
}