package world.ssafy.tourtalk.controller;

import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.Member;
import world.ssafy.tourtalk.model.service.MemberService;
import world.ssafy.tourtalk.security.jwt.JwtTokenProvider;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final MemberService mService;
	private final JwtTokenProvider jwtTokenProvider;
	
	// 로그인
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String id, @RequestParam String password, HttpServletResponse response) {
		try {
			Member member = mService.login(id, password);
			if(member != null) {
				String jwtToken = jwtTokenProvider.createToken(member.getMno(), member.getId(), member.getNickname(),member.getRole());
				
				Cookie token = new Cookie("token", jwtToken);
				token.setHttpOnly(true);
				token.setSecure(false);
				token.setPath("/");
				token.setMaxAge(60 * 60);
				// token.setDomain();
				
				response.addCookie(token);
				
				return ResponseEntity.ok(Map.of(
					    "message", "로그인 성공",
					    "nickname", member.getNickname(),
					    "role", member.getRole(),
					    "id", member.getId()
					));
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 올바르지 않습니다!");				
			}
		} catch (DataAccessException e) {
			log.error("로그인 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		Cookie token = new Cookie("token", null);
		token.setHttpOnly(true);
		token.setSecure(false);
		token.setPath("/");
		token.setMaxAge(0);
		
		response.addCookie(token);
		
		return ResponseEntity.ok("로그아웃 완료");
	}
	
	@GetMapping("/check")
	public ResponseEntity<?> checkLoginStatus() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    // 인증되지 않았을 경우 anonymousUser로 설정됨
	    if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Integer)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
	    }

	    Integer mno = (Integer) auth.getPrincipal();
	    return ResponseEntity.ok("로그인됨: mno = " + mno);
	}
	
}
