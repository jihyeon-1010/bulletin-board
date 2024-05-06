package com.example.bulletinboardapp.entity;

import com.example.bulletinboardapp.util.SocialType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;
    private String nickname;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now();
    }

    public User(SocialType socialType, String socialId, String email, String nickname) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.email = email;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now();
    }

    // 비밀번호 업데이트
    public void updatePassword(String password) {
        this.password = password;
    }

    // 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    // 사용자 id 반환
    @Override
    public String getUsername() {
        return getEmail();
    }

    // 사용자 패스워드 반환
    @Override
    public String getPassword() {
        return password;
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        // true: 계정이 만료되지 않음
        return true;
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        // true: 계정이 잠금되지 않음
        return true;
    }

    // 패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        // true: 패스워드가 만료되지 않음
        return true;
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        // true: 계정 사용 가능함
        return true;
    }
}
