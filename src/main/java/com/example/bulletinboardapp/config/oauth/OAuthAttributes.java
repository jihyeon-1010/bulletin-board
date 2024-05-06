package com.example.bulletinboardapp.config.oauth;

import com.example.bulletinboardapp.config.userinfo.GoogleOAuth2UserInfo;
import com.example.bulletinboardapp.config.userinfo.KakaoOAuth2UserInfo;
import com.example.bulletinboardapp.config.userinfo.NaverOAuth2UserInfo;
import com.example.bulletinboardapp.config.userinfo.OAuth2UserInfo;
import com.example.bulletinboardapp.entity.User;
import com.example.bulletinboardapp.util.SocialType;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

// 각 소셜별로 받는 데이터를 분기 처리하는 DTO 클래스
@Getter
public class OAuthAttributes {
    private String nameAttributeKey; // 로그인 진행 시 키가 되는 필드 값 (PK)
    private OAuth2UserInfo oAuth2UserInfo; // 소셜 타입 별 로그인 유저 정보(닉네임, 이메일 등)

    private OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    // SocialType에 맞는 메서드를 호출하여 OAuthAttributes 객체 반환
    // 파라미터: userNameAttributeName -> OAuth2 로그인 시 키(PK)가 되는 값
    // 소별별 of 메서드(ofGoogle, ofKakao, ofNaver)들은 각각 소셜 로그인 API에서 제공하는 회원의 식별값(ID), attributes, nameAttributeKey를 저장 후 build

    // CustomOAuth2UserService에서 파라미터들을 주입해 OAuthAttributes 객체를 생성하는 메서드
    // 파라미터로 들어온 SocialType별로 분기 처리하여 각 소셜 타입에 맞게 OAuthAttributes 생성
    public static OAuthAttributes of(
            SocialType socialType,
            String userNameAttributeName,
            Map<String, Object> attributes) {
        if (socialType == SocialType.NAVER) {
            return ofNaver(userNameAttributeName, attributes);
        }
        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    public static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuthAttributes(userNameAttributeName, new KakaoOAuth2UserInfo(attributes));
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuthAttributes(userNameAttributeName, new GoogleOAuth2UserInfo(attributes));
    }

    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuthAttributes(userNameAttributeName, new NaverOAuth2UserInfo(attributes));
    }

    // of 메서드로 OAuthAttributes 객체가 생성되어, 유저 정보들이 담긴 OAuth2UserInfo가 소셜 타입별로 주입된 상태
    // OAuth2UserInfo에서 socialID(식별값), nickname을 가져와서 빌드
    // email에는 uuid로 중복 없는 랜덤 값 생성
    // role은 GUEST로 설정
    public User toEntity(SocialType socialType, OAuth2UserInfo oAuth2UserInfo) {
        String email = UUID.randomUUID() + "@socialUser.com"; // JWT Token을 발급하기 위한 용도일 뿐이므로 uuid를 사용하여 임의로 생성

        return new User(
                socialType,
                oAuth2UserInfo.getId(),
                oAuth2UserInfo.getEmail() != null ? oAuth2UserInfo.getEmail() : email,
                oAuth2UserInfo.getNickname());
    }
}
