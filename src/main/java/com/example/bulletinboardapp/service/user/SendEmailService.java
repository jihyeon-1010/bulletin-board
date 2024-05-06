package com.example.bulletinboardapp.service.user;

import com.example.bulletinboardapp.dto.user.response.MailDto;
import com.example.bulletinboardapp.entity.User;
import com.example.bulletinboardapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Slf4j
@Service
public class SendEmailService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserService userService;

    private JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "l91229122leejh@gmail.com";

    @Transactional
    public MailDto sendMailAndChangePassword(String userEmail) {
        String tempPassword = getTempPassword();
        MailDto mailDto = new MailDto();

        String userNickname = updatePassword(userEmail, tempPassword);

        mailDto.setAddress(userEmail);
        mailDto.setTitle("HELLO 게시판 임시 비밀번호 안내");
        mailDto.setMessage("안녕하세요. HELLO 게시판 임시 비밀번호 안내 관련 이메일입니다. \n \n" +
                userNickname + " 회원님의 임시 비밀번호는 " + tempPassword + "입니다. \n \n");
        updatePassword(userEmail, tempPassword);

        return mailDto;
    }

    // 유저 비밀번호 업데이트
    @Transactional
    public String updatePassword(String userEmail, String password) {
        // 1. 패스워드 인코딩
        String newPassword = encoder.encode(password);

        // 2. 타겟 유저 가져오기
        User user = userService.findByEmail(userEmail);

        // 3. 비밀번호 업데이트
        user.updatePassword(newPassword);

        // 4. 유저 닉네임 반환
        return user.getNickname();
    }

    // 난수로 임시 비밀번호 생성
    @Transactional
    public String getTempPassword() {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        StringBuilder tempPassword = new StringBuilder();

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            tempPassword.append(charSet[idx]);
        }

        return tempPassword.toString();
    }

    // 사용자 메일로 임시 비밀번호 전송
    public void mailSend(MailDto mailDto) {
        log.info(mailDto.getAddress() + "로 임시 비밀번호 전송 완료");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getAddress()); // 받는 사람 주소
        message.setFrom(SendEmailService.FROM_ADDRESS); // 보내는 사람 주소 (만약 설정하지 않았으면 yml 파일에 작성한 'username' 값으로 적용됨
        message.setSubject(mailDto.getTitle()); // 메일 제목
        message.setText(mailDto.getMessage()); // 메일 내용

        mailSender.send(message);
    }
}
