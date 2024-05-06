package com.example.bulletinboardapp.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {
    // 요청값(이름, 값, 만료 기간)을 바탕으로 HTTP 응답에 쿠키 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    // 쿠키의 이름을 입력받아 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        // 요청으로부터 모든 쿠키 가져오기
        Cookie[] cookies = request.getCookies();
        // 만약 쿠키가 존재하지 않으면 처리 중단
        if (cookies == null) {
            return;
        }

        //
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue(""); // 파라미터로 넘어온 키의 쿠키를 빈 값으로 변경
                cookie.setPath("/"); // 쿠키 경로 설정
                cookie.setMaxAge(0); // 만료 시간을 0으로 설정해 즉시 만료 처리
                response.addCookie(cookie); // 변경된 쿠키를 응답에 추가
            }
        }
    }

    // 주어진 객체를 직렬화해 쿠키의 값으로 변환
    public static String serialize(Object obj) {
        // 주어진 객체를 직렬화하여 바이트 배열로 변환한 후, Base64 인코딩을 사용하여 문자열로 변환
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    // 쿠키를 역직렬화해 객체로 변환
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}
