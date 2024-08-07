package hello.blog.service;

import hello.blog.domain.SocialUser;
import hello.blog.domain.User;
import hello.blog.repository.SocialUserRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialUserService extends DefaultOAuth2UserService {

    private final SocialUserRepository socialUserRepository;
    private final UserRepository userRepository;

    // 소셜에서 정보를 가져와서 저장하거나, 이미 있다면 수정하거나..
    @Transactional
    public SocialUser saveOrUpdateUser(String socialId, String provider, String username, String email, String avatarUrl) {
        Optional<SocialUser> existingUser = socialUserRepository.findBySocialIdAndProvider(socialId, provider);

        SocialUser socialUser;

        if (existingUser.isPresent()) {
            socialUser = existingUser.get();
            socialUser.setUsername(username);
            socialUser.setEmail(email);
            socialUser.setAvatarUrl(avatarUrl);
        } else {
            socialUser = new SocialUser();
            socialUser.setSocialId(socialId);
            socialUser.setProvider(provider);
            socialUser.setUsername(username);
            socialUser.setEmail(email);
            socialUser.setAvatarUrl(avatarUrl);
        }

        SocialUser savedSocialUser = socialUserRepository.save(socialUser);

        Optional<User> existingMainUser = userRepository.findByProviderAndSocialId(provider, socialId);

        if (existingMainUser.isPresent()) {
            User mainUser = existingMainUser.get();
            mainUser.setUserName(username);
            mainUser.setEmail(email);
            mainUser.setSocialId(socialId);
            mainUser.setProvider(provider);
            userRepository.save(mainUser);
        } else {
            User mainUser = new User();
            mainUser.setUserName(username);
            mainUser.setEmail(email);
            mainUser.setSocialId(socialId);
            mainUser.setProvider(provider);
            mainUser.setPassword(""); // 소셜 로그인 사용자는 비밀번호를 사용하지 않습니다.
            userRepository.save(mainUser);
        }

        return savedSocialUser;
    }
}