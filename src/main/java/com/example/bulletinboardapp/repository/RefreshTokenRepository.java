package com.example.bulletinboardapp.repository;

import com.example.bulletinboardapp.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // 사용자 id에 해당하는 리프레시 토큰 반환
    Optional<RefreshToken> findByUserId(Long userId);

    // 주어진 리프레시 토큰에 해당하는 리프레시 토큰 반환
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
