package world.ssafy.tourtalk.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 관리자 API 키 검증 인터셉터
 */
@Slf4j
@Component
public class AdminApiKeyInterceptor implements HandlerInterceptor {
    
    @Value("${admin.api.key}")
    private String adminApiKey;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 요청은 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // API 키 헤더 확인
        String apiKey = request.getHeader("X-Admin-API-Key");
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Missing API key for admin endpoint: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"API 키가 필요합니다.\"}");
            return false;
        }
        
        if (!adminApiKey.equals(apiKey)) {
            log.warn("Invalid API key for admin endpoint: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"유효하지 않은 API 키입니다.\"}");
            return false;
        }
        
        log.info("Valid API key for admin endpoint: {}", request.getRequestURI());
        return true;
    }
}