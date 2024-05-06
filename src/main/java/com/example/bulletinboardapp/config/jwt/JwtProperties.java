package com.example.bulletinboardapp.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt") // 자바 클래스에 프로퍼티값을 가져와 사용하는 어노테이션
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
