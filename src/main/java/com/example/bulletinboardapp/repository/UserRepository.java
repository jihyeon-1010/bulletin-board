package com.example.bulletinboardapp.repository;

import com.example.bulletinboardapp.entity.User;
import com.example.bulletinboardapp.util.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 사용자 정보 가져오기
    Optional<User> findByEmail(String email);

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
