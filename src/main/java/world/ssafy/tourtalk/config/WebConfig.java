package world.ssafy.tourtalk.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final AdminApiKeyInterceptor adminApiKeyInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 관리자 API 경로에 대해서만 인터셉터 적용
        registry.addInterceptor(adminApiKeyInterceptor)
                .addPathPatterns("/api/v1/admin/**")
                .excludePathPatterns("/api/v1/admin/health"); // 헬스체크는 제외
    }
}