package world.ssafy.tourtalk.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtFilter;
	
	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		// CSRF 보호는 비활성화 (JWT 기반 API에서는 필요 없음)
		.csrf(csrf -> csrf.disable())
		// 세션을 생성하지 않도록 설정 (Stateless)
		.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		// 경로 기반 권한 설정
		.authorizeHttpRequests(auth -> auth
				// 추후 JWT까지 구현한다면 프론트 페이지를 수정할 필요가 있음. 지금은 /secured, /auth 경로 x
				// user 권한을 가지고 있는 사용자만 /secured/user/~~ 경로에 접근 가능
				.requestMatchers("/secured/user/**").hasRole("USER") 
				.requestMatchers("/secured/curator/**").hasRole("CURATOR")
				// admin 권한을 가지고 있는 사용자만 /secured/admin/~~ 경로에 접근 가능
				.requestMatchers("/secured/admin/**").hasRole("ADMIN")
				// 로그인을 한 상태면 /auth/~~ 경로에 접근 가능
				.requestMatchers("/auth/**").authenticated()
				// 그 외의 경로는 접속을 하지 않아도 모두 접근 가능
				.anyRequest().permitAll()
				)
		// JWT 필터를 UsernamePasswordAuthenticationFilter 이전에 삽입
		.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}
