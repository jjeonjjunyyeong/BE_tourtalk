package world.ssafy.tourtalk.controller;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.Member;
import world.ssafy.tourtalk.model.service.MemberService;
import world.ssafy.tourtalk.security.jwt.JwtTokenProvider;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

	private final MemberService mService;
	private final JwtTokenProvider jwtTokenProvider;

	// 회원가입
	@PostMapping("/regist")
	public ResponseEntity<?> regist(@RequestBody Member member) {
		try {
			int result = mService.regist(member);
			return result > 0 ? ResponseEntity.status(HttpStatus.OK).body("회원가입 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패!!!");
		} catch (DataAccessException e) {
			log.error("회원가입 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestParam String id, @RequestParam String password, HttpServletResponse response) {
		try {
			Member member = mService.login(id, password);
			if(member != null) {
				String jwtToken = jwtTokenProvider.createToken(member.getId(), member.getRole());
				
				Cookie token = new Cookie("token", jwtToken);
				token.setHttpOnly(true);
				token.setSecure(false);
				token.setPath("/");
				token.setMaxAge(60 * 60);
				
				response.addCookie(token);
				
				return ResponseEntity.status(HttpStatus.OK).body("로그인 성공 !");
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패!!!");				
			}
		} catch (DataAccessException e) {
			log.error("로그인 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 회원 정보 수정
	@PutMapping
	public ResponseEntity<?> modify(@RequestBody Member member) {
		try {
			int result = mService.modify(member);
			return result > 0 ? ResponseEntity.status(HttpStatus.OK).body("회원정보 수정 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원정보 수정 실패!!!");
		} catch (DataAccessException e) {
			log.error("회원정보 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 회원 정보 조회(본인)
	@GetMapping("/me")
	public ResponseEntity<?> memberInfo(@RequestParam String id) {
		try {
			Member member = mService.memberInfo(id);
			return member != null ? ResponseEntity.ok(member)
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원정보 수정 실패!!!");
		} catch (DataAccessException e) {
			log.error("회원정보 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 회원 탈퇴
	@DeleteMapping
	public ResponseEntity<?> delete(@RequestParam String id) {
		try {
			int result = mService.delete(id);
			return result > 0 ? ResponseEntity.status(HttpStatus.OK).body("회원정보 수정 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원정보 수정 실패!!!");
		} catch(DataAccessException e) {
			log.error("회원정보 수정 중 오류 발생", e);
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
}
