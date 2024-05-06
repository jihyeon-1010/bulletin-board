package com.example.bulletinboardapp.config.userinfo;

import java.util.Map;

public abstract class OAuth2UserInfo {
    // 추상 클래스를 상속받는 클래스에서만 사용할 수 있도록 protected 제어자 사용
    protected Map<String, Object> attributes;

    // 파라미터로 각 소셜 타입별 유저 정보 attributes를 주입받음
    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId(); // 소셜 식별 값: 구글 - "sub", 카카오 = "-id", 네이버 = "id"

    public abstract String getEmail();

    public abstract String getNickname();
}
