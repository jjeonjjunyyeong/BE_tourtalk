package world.ssafy.tourtalk.controller;

import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;
import world.ssafy.tourtalk.model.service.AuthService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;
import world.ssafy.tourtalk.security.jwt.JwtTokenProvider;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService aService;
	private final JwtTokenProvider jwtTokenProvider;
	
	// 로그인
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String id, @RequestParam String password, HttpServletResponse response) {
		log.info("=== 로그인 시도 ===");
		log.info("요청 ID: {}", id);
		log.debug("요청 시간: {}", java.time.LocalDateTime.now());
		
		try {
			MemberResponse member = aService.login(id, password);
			if(member != null) {
				log.info("로그인 성공 - 사용자: {}", member.getNickname());
				
				String jwtToken = jwtTokenProvider.createToken(member.getMno(), member.getId(), member.getNickname(), member.getRole());
				log.debug("JWT 토큰 생성 완료");
				
				Cookie token = new Cookie("token", jwtToken);
				token.setHttpOnly(true);
				token.setSecure(false);
				token.setPath("/");
				token.setMaxAge(60 * 60);
				// token.setDomain();
				
				response.addCookie(token);
				log.debug("쿠키 설정 완료");
				
				return ResponseEntity.ok(Map.of(
					    "message", "로그인 성공",
					    "nickname", member.getNickname(),
					    "role", member.getRole(),
					    "id", member.getId()
					));
			} else {
				log.warn("로그인 실패 - 잘못된 인증 정보, 요청 ID: {}", id);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 올바르지 않습니다!");				
			}
		} catch (DataAccessException e) {
			log.error("로그인 중 데이터베이스 오류 발생 - 요청 ID: {}, 오류: {}", id, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		log.info("=== 로그아웃 요청 ===");
		log.debug("로그아웃 시간: {}", java.time.LocalDateTime.now());
		
		Cookie token = new Cookie("token", null);
		token.setHttpOnly(true);
		token.setSecure(false);
		token.setPath("/");
		token.setMaxAge(0);
		
		response.addCookie(token);
		log.info("로그아웃 완료 - 쿠키 삭제됨");
		
		return ResponseEntity.ok("로그아웃 완료");
	}
	
	// 로그인 상태 확인
	@GetMapping("/check")
	public ResponseEntity<?> checkLoginStatus(@AuthenticationPrincipal CustomMemberPrincipal principal) {
		log.debug("=== 로그인 상태 확인 ===");
		log.debug("확인 시간: {}", java.time.LocalDateTime.now());
		
		if (principal == null) {
			log.warn("로그인 상태 확인 결과: 미인증 사용자");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
		}
		
		log.info("로그인 상태 확인 결과: 인증된 사용자 - {}", principal.getNickname());
		log.debug("사용자 정보 - ID: {}, 역할: {}", principal.getId(), principal.getRole());
		
		return ResponseEntity.ok(Map.of(
				"message", "로그인됨",
				"mno", principal.getMno(),
				"id", principal.getId(),
				"nickname", principal.getNickname(),
				"role", principal.getRole()
		));
	}
	
}