package com.example.bulletinboardapp.service.user;

import com.example.bulletinboardapp.entity.User;
import com.example.bulletinboardapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 스프링 시큐리티에서 로그인을 진행할 때 사용자 정보를 가져오는 서비스 코드
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    // 사용자 이름(이메일)로 사용자 정보를 가져오는 메서드
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }
}
