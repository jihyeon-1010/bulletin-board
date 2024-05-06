package com.example.bulletinboardapp.config.oauth;

import com.example.bulletinboardapp.config.CustomOAuth2User;
import com.example.bulletinboardapp.entity.User;
import com.example.bulletinboardapp.repository.UserRepository;
import com.example.bulletinboardapp.util.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    // 리소스 서버에서 보내주는 사용자 정보를 불러오는 메서드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청");
        // DefaultOAuth2UserService 객체를 생성하여 loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
        // DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서 사용자 정보를 얻은 후,
        // 이를 통해 DefaultOAuth2User 객체를 생성 후 반환
        // 즉, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저임
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
        // http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
        //userNameAttributeName은 이후에 nameAttributeKey로 설정
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest  // 소셜 로그인 시 키(PK)가 되는 값
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes(); // 소셜 로그인에서 api가 제공하는 userInfo의 json 값(유저 정보)

        // socialType에 따라 OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

        User createdUser = getUser(extractAttributes, socialType);

        // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdUser.getEmail()
        );
    }

    private SocialType getSocialType(String registrationId) {
        if (NAVER.equals(registrationId)) {
            return SocialType.NAVER;
        }
        if (KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        }
        return SocialType.GOOGLE;
    }

    // socialType과 attributes에 들어있는 소셜 로그인의 식별값 id를 통해 회원을 찾아 반환하는 메서드
    // 만약 찾은 회원이 있다면 그대로 반환하고, 없다면 회원 저장
    private User getUser(OAuthAttributes attributes, SocialType socialType) {
        User findUser = userRepository.findBySocialTypeAndSocialId(socialType, attributes.getOAuth2UserInfo().getId()).orElse(null);

        if (findUser == null) {
            return saveUser(attributes, socialType);
        }
        return findUser;
    }

    private User saveUser(OAuthAttributes attributes, SocialType socialType) {
        User createdUser = attributes.toEntity(socialType, attributes.getOAuth2UserInfo());
        return userRepository.save(createdUser);
    }
}
