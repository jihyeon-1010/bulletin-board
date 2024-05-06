package com.example.bulletinboardapp.config.userinfo;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo{
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        String email = (String) attributes.get("email");

        if (email == null) {
            return null;
        }

        return email;
    }

    @Override
    public String getNickname() {
        String nickname = (String) attributes.get("name");

        if (nickname == null) {
            return null;
        }

        return nickname;
    }
}
