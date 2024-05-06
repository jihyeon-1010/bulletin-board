package com.example.bulletinboardapp.config;

import com.example.bulletinboardapp.config.handler.form.LoginFailureHandler;
import com.example.bulletinboardapp.config.handler.form.LoginSuccessHandler;
import com.example.bulletinboardapp.config.handler.oauth.OAuth2FailureHandler;
import com.example.bulletinboardapp.config.handler.oauth.OAuth2SuccessHandler;
import com.example.bulletinboardapp.config.jwt.TokenProvider;
import com.example.bulletinboardapp.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.bulletinboardapp.config.oauth.OAuth2UserCustomService;
import com.example.bulletinboardapp.repository.RefreshTokenRepository;
import com.example.bulletinboardapp.service.user.UserDetailService;
import com.example.bulletinboardapp.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final UserDetailService userDetailService;

    // 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/mysql-console/**"))
                .requestMatchers(new AntPathRequestMatcher("/img/**"))
                .requestMatchers(new AntPathRequestMatcher("/css/**"))
                .requestMatchers(new AntPathRequestMatcher("/js/**"))
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain oauthFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
            throws Exception {
         http

                 .httpBasic(httpBasic -> httpBasic.disable());
         http
                 // 헤더를 확인할 커스텀 필터 추가
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

         http
                 // 토큰 재발급, 로그인, 회원가입 URL은 인증 없이 접근 가능, 나머지 API URL은 인증 필요
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new MvcRequestMatcher(introspector, "/signup")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/user")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/login")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/token")).permitAll()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/articles/**")).authenticated()
                        .requestMatchers(new MvcRequestMatcher(introspector, "/api/**")).authenticated()
                        .anyRequest().permitAll());
         http
                 .formLogin(login -> login
                         .loginPage("/login")
                         .successHandler(loginSuccessHandler())
                         .failureHandler(loginFailureHandler())
                 );

         http
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .authorizationEndpoint(endpoint -> endpoint
                                // Authorization 요청과 관련된 상태를 저장할 저장소 설정 (세션이 아닌 쿠키에 저장할 수 있도록)
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .successHandler(oAuth2SuccessHandler()) // 인증 성공 시 실행할 핸들러
                        .failureHandler(oAuth2FailureHandler()) // 인증 실패 시 실행할 핸들러
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserCustomService)));

         http
                 .logout(logout -> logout
                        .logoutSuccessUrl("/login"));

         http
                 // /api로 시작하는 URL에 인증되지 않은 상태로 접근 시 401 상태 코드를 반환하도록 예외 처리
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")));

        http
                .csrf((csrf) -> csrf.disable());

         return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                    tokenProvider,
                    refreshTokenRepository,
                    oAuth2AuthorizationRequestBasedOnCookieRepository(),
                    userService
                );
    }

    @Bean
    public OAuth2FailureHandler oAuth2FailureHandler() {
        return new OAuth2FailureHandler();
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    // 인증 관리자 관련 설정
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailService); // 사용자 정보 서비스 설정
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
