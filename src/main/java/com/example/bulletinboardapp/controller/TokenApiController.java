package com.example.bulletinboardapp.controller;

import com.example.bulletinboardapp.dto.token.request.CreateAccessTokenRequest;
import com.example.bulletinboardapp.dto.token.response.CreateAccessTokenResponse;
import com.example.bulletinboardapp.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) {
        // 전달받은 리프레시 토큰을 사용하여 새로운 액세스 토큰 생성
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        // 새로운 액세스 토큰을 응답 본문에 담아 클라이언트에게 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}
