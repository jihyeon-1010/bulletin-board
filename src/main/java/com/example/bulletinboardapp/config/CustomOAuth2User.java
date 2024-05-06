package com.example.bulletinboardapp.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {
    private String email;

    public CustomOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey,
            String email) {
        super(authorities, attributes, nameAttributeKey); // 부모 객체인 DefaultOAuth2User 생성
        // email 추가 주입
        this.email = email;
    }
}
