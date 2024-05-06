package com.example.bulletinboardapp.service.user;

import com.example.bulletinboardapp.config.CustomOAuth2User;
import com.example.bulletinboardapp.dto.user.request.CreateUserRequest;
import com.example.bulletinboardapp.entity.User;
import com.example.bulletinboardapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Map;

// 회원 정보를 추가하는 서비스 코드
@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    // 회원가입
    @Transactional
    public void signUp(CreateUserRequest dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 1. 예외 처리
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 2. 유저 저장
        userRepository.save(new User(
            dto.getEmail(),
            encoder.encode(dto.getPassword()),
            dto.getNickname()));
    }

    // 유저 id로 유저를 검색한 후 반환
    @Transactional
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 정보가 없습니다."));
    }

    // 유저 이메일로 유저를 검색한 후 반환
    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));
    }

    @Transactional
    public boolean userEmailCheck(String userEmail) {
        // 1. 이메일로 유저 검색
        User user = userRepository.findByEmail(userEmail).orElse(null);

        // 2. DB에 입력받은 유저 이메일이 있다면 true 반환
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    // 현재 인증된 유저 반환
    @Transactional
    public User getCurrentUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }
}
