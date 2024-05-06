package com.example.bulletinboardapp.config.handler.form;

import com.example.bulletinboardapp.config.jwt.TokenProvider;
import com.example.bulletinboardapp.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.bulletinboardapp.entity.RefreshToken;
import com.example.bulletinboardapp.entity.User;
import com.example.bulletinboardapp.repository.RefreshTokenRepository;
import com.example.bulletinboardapp.service.user.UserService;
import com.example.bulletinboardapp.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token"; // 리프레시 토큰을 쿠키에 저장할 때 사용되는 쿠키의 이름
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14); // 리프레시 토큰 유효기간 (2주)
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1); // 액세스 토큰의 유효 기간(1일)
    public static final String REDIRECT_PATH = "/articles"; // 인증 성공 후 리다이렉트할 경로

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    // 사용자가 성공적으로 인증될 때 호출
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        String email = extractUsername(authentication); // 인증 정보에서 email 추출

        User user = userService.findByEmail(email);

        // ---------------- 리프레시 토큰 ----------------
        // 토큰 제공자를 사용해 리프레시 토큰 생성
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        // 해당 리프레시 토큰을 유저 ID와 함께 DB에 저장
        saveRefreshToken(user.getId(), refreshToken);
        // 클라이언트에서 액세스 토큰이 만료되면 재발급 요청하도록 쿠키에 리프레리 토큰 저장
        addRefreshTokenToCookie(request, response, refreshToken);

        // ---------------- 액세스 토큰 ----------------
        // 토큰 제공자를 사용해 액세스 토큰 생성
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        // 쿠키에서 리다이렉트 경로가 담긴 값을 가져와 패스에 액세스 토큰 추가
        String targetUrl = getTargetUrl(accessToken);

        // 인증 프로세스를 진행하면서 세션과 쿠키에 임시로 저장해둔 인증 관련 데이터 제거
        clearAuthenticationAttributes(request, response);

        // 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 현재 인증된 사용자 이메일 반환
    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    // 유저 id와 생성된 리프레시 토큰을 전달받아 DB에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);

        log.info("리프레시 토큰이 저장되었습니다. RefreshToken : {}", refreshToken);
    }

    // 생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);

        log.info("리프레시 토큰이 쿠키에 저장되었습니다. RefreshToken : {}", refreshToken);
    }

    // 인증 관련 설정값, 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    // 액세스 토큰을 패스에 추가
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
