package world.ssafy.tourtalk.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 쿠키 기반 임시 사용자 관리
 */
@Component
public class TemporaryUserService {
    
    private static final String USER_ID_COOKIE_NAME = "temp_user_id";
    private static final int COOKIE_MAX_AGE = 30 * 24 * 60 * 60; // 30일
    
    /**
     * 현재 사용자 ID 조회 (없으면 생성)
     */
    public String getCurrentUserId(HttpServletRequest request, HttpServletResponse response) {
        // 기존 쿠키에서 사용자 ID 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (USER_ID_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        // 쿠키가 없으면 새로 생성
        String newUserId = generateUserId();
        Cookie userCookie = new Cookie(USER_ID_COOKIE_NAME, newUserId);
        userCookie.setMaxAge(COOKIE_MAX_AGE);
        userCookie.setPath("/");
        userCookie.setHttpOnly(true);
        response.addCookie(userCookie);
        
        return newUserId;
    }
    
    /**
     * 사용자 ID 생성
     */
    private String generateUserId() {
        return "temp_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}