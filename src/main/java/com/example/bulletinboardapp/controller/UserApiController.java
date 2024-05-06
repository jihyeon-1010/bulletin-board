package com.example.bulletinboardapp.controller;

import com.example.bulletinboardapp.dto.user.request.CreateUserRequest;
import com.example.bulletinboardapp.dto.user.request.SendEmailRequest;
import com.example.bulletinboardapp.dto.user.response.MailDto;
import com.example.bulletinboardapp.service.user.SendEmailService;
import com.example.bulletinboardapp.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class UserApiController {
    private final UserService userService;
    private final SendEmailService sendEmailService;

    // 회원가입
    @PostMapping("/user")
    public String signUp(CreateUserRequest request) {
        userService.signUp(request);

        return "redirect:/login";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());

        return "redirect:/login";
    }

    // 비밀번호 찾기 페이지
    @GetMapping("/password/reset")
    public String resetPassword() {
        return "auth/resetPassword";
    }

    // 이메일 체크
    @GetMapping("/check/findPw")
    public ResponseEntity<Boolean> findPw(@RequestParam String userEmail) {
        boolean findPwCheck = userService.userEmailCheck(userEmail);

        return ResponseEntity.ok(findPwCheck);
    }

    // 임시 비밀번호 메일 발송
    @PostMapping("/check/findPw/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody SendEmailRequest request) {
        MailDto mailDto = sendEmailService.sendMailAndChangePassword(request.getUserEmail());
        sendEmailService.mailSend(mailDto);

        return ResponseEntity.ok("이메일을 성공적으로 발송했습니다.");
    }
}
