package world.ssafy.tourtalk.controller;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.Member;
import world.ssafy.tourtalk.model.dto.MemberDetails;
import world.ssafy.tourtalk.model.dto.reqeust.MemberRegistRequest;
import world.ssafy.tourtalk.model.dto.reqeust.MemberUpdateRequest;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;
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
	@PostMapping
	public ResponseEntity<?> regist(@RequestBody MemberRegistRequest request) {
		try {
			int result = mService.regist(request);
			return result > 1 ? ResponseEntity.status(HttpStatus.OK).body("회원가입 성공 !")
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
	
	// 회원 정보 조회(본인)
	@GetMapping("/me")
	public ResponseEntity<?> me(HttpServletRequest request) {
		try {
			String token = jwtTokenProvider.resolveToken(request);
			if(token == null || !jwtTokenProvider.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다.");
			
			String id = jwtTokenProvider.getUserId(token);
			MemberResponse response = mService.me(id);
			
			if(response == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원을 찾을 수 없습니다.");
			return ResponseEntity.ok(response);
		} catch (DataAccessException e) {
			log.error("회원정보 조회 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 회원 정보 수정
	@PutMapping("/me")
	public ResponseEntity<?> update(@RequestBody MemberUpdateRequest  request) {
		try {
			int result = mService.update(request);
			return result > 1 ? ResponseEntity.status(HttpStatus.OK).body("회원정보 수정 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원정보 수정 실패!!!");
		} catch (DataAccessException e) {
			log.error("회원정보 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 회원 탈퇴
	@DeleteMapping("/me")
	public ResponseEntity<?> delete(HttpServletRequest request) {
		try {
			String token = jwtTokenProvider.resolveToken(request);
			if(token == null || !jwtTokenProvider.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다.");
			String id = jwtTokenProvider.getUserId(token);
			int result = mService.delete(id);
			return result > 0 ? ResponseEntity.status(HttpStatus.OK).body("회원탈퇴 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원탈퇴 실패!!!");
		} catch(DataAccessException e) {
			log.error("회원정보 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
}
