package com.example.bulletinboardapp.service.token;

import com.example.bulletinboardapp.config.jwt.TokenProvider;
import com.example.bulletinboardapp.entity.User;
import com.example.bulletinboardapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    // 새로운 액세스 토큰 생성 메서드
    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        // 유효한 리프레시 토큰을 사용하여 사용자 Id 찾기
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        // 찾은 사용자 ID를 사용하여 해당 사용자 정보를 DB에서 가져오기
        User user = userService.findById(userId);

        // id로 찾은 사용자의 새로운 액세스 토큰 생성
        // 새로운 액세스 토큰의 유효기간은 2시간으로 설정
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
