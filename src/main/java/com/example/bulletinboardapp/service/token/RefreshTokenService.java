package com.example.bulletinboardapp.service.token;

import com.example.bulletinboardapp.entity.RefreshToken;
import com.example.bulletinboardapp.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    // 전달받은 리프레시 토큰으로 리프레시 토큰 객체를 검사해서 반환
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("해당 토큰을 찾을 수 없습니다."));
    }
}
