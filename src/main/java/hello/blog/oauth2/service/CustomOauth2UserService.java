package hello.blog.oauth2.service;

import hello.blog.repository.RoleRepository;
import hello.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

//        if (registrationId.equals("naver")) {
//            log.info("naver 로그인");
//            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
//        } else if (registrationId.equals("kakao")) {
//            log.info("kakao 로그인");
//            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
//        }else {
//            System.out.println("로그인 실패");
//            throw new IllegalArgumentException("지원하지 않는 로그인 제공자입니다.");
//        }

        switch (registrationId) {
            case "naver":
                log.info("naver 로그인");
                oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
                break;

            case "kakao":
                log.info("kakao 로그인");
                oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
                break;

            case "google":
                log.info("google 로그인");
                oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
                break;

            default:
                log.error("로그인 실패: 지원하지 않는 로그인 제공자입니다. 등록 ID: {}", registrationId);
                throw new IllegalArgumentException("지원하지 않는 로그인 제공자입니다.");
        }


        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();

        String name = provider + " " + providerId; // 이렇게 해서 해당 유저가 이미 디비에 있는지 없는지 확인
        Optional<User> userOptional = userRepository.findByUsername(name);

        // "USER" 라는 역할을 OAuth2 로그인 사람에게 다 부여
        String roleName = "ROLE_USER";
        Optional<Role> roleOptional = roleRepository.findByName(roleName); // 디비에서 찾아오는데,
        Role role;
        if (roleOptional.isEmpty()) { // "USER" 디비에 없다면
            role = new Role(roleName); // 새로운 역할 등록
            role = roleRepository.save(role);
        } else {
            role = roleOptional.get(); // 그게 아니라면 역할 꺼내오기
        }

        String password = String.valueOf(UUID.randomUUID());

        User user;
        // OAuth2 로그인을 한 적 없는 사람
        if (userOptional.isEmpty()) {
            user = User.builder()
                    .name(oAuth2Response.getEmail())
                    .username(name)
                    .roles(Set.of(role))
                    .providerId(oAuth2Response.getProviderId())
                    .provider(oAuth2Response.getProvider())
                    .password(password)
                    .phoneNumber("default")
                    .birthdate(LocalDate.from(LocalDateTime.now()))
                    .gender("default")
                    .registrationDate(LocalDateTime.now())
                    .usernick(oAuth2Response.getName())
                    .build();
            userRepository.save(user);
        } else { // 이미 OAuth2 로그인을 한 적이 있는 사람
            user = userOptional.get();
            user.setUsername(name);
            user.setName(oAuth2Response.getEmail());
            user.setProviderId(oAuth2Response.getProviderId());
            user.setProvider(oAuth2Response.getProvider());
            user.getRoles().add(role);
            user.setPhoneNumber("dafault");
            user.setRegistrationDate(LocalDateTime.now());
            user.setUsernick(oAuth2Response.getName());
            userRepository.save(user);
            roleName = user.getRoles().iterator().next().getName();
        }

        System.out.println("User saved: " + user);

        // 특정 사이트의 응답 값과 역할을 받는 CustomOAuth2User 클래스
        // 로그인 한적 없는 사람은 "USER" 역할, 기존에 한 적있다면 그 사람이 현재 갖고 있는 역할을 CustomOAuth2User 클래스로 반환
        return new CustomOAuth2User(oAuth2Response, roleName);
    }
}