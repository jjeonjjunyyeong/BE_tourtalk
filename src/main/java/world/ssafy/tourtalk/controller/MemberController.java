package world.ssafy.tourtalk.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.request.MemberRequest;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;
import world.ssafy.tourtalk.model.service.MemberService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

	private final MemberService mService;

	// 회원가입
	@PostMapping
	public ResponseEntity<?> regist(@RequestBody MemberRequest request) {
		try {
			boolean success = mService.regist(request);
			
			if (success) {
				return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공 !");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패!!!");
			}
		} catch (DataAccessException e) {
			log.error("회원가입 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 회원 정보 조회(본인)
	@GetMapping("/me")
	public ResponseEntity<?> me(@AuthenticationPrincipal CustomMemberPrincipal principal) {
		try {
			int mno = principal.getMno();
			MemberResponse response = mService.me(mno);
			return response != null
					? ResponseEntity.ok(response)
					: ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원을 찾을 수 없습니다.");
		} catch (DataAccessException e) {
			log.error("회원정보 조회 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 회원 정보 수정
	@PutMapping("/me")
	public ResponseEntity<?> update(@RequestBody MemberRequest request, @AuthenticationPrincipal CustomMemberPrincipal principal) {
		try {
			request.setMno(principal.getMno());
			boolean success = mService.update(request);
			
			return success
					? ResponseEntity.status(HttpStatus.CREATED).body("회원정보 수정 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원정보 수정 실패!!!");
		} catch (DataAccessException e) {
			log.error("회원정보 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 회원 탈퇴
	@DeleteMapping("/me")
	public ResponseEntity<?> softDelete(@AuthenticationPrincipal CustomMemberPrincipal principal) {
		try {
			Integer mno = principal.getMno();
			String nickname = principal.getNickname();
			
			boolean success = mService.softDelete(mno);
			
			if (success) {
				return ResponseEntity.status(HttpStatus.CREATED).body(nickname + "님 회원탈퇴 성공 !");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원탈퇴 실패!!!");
			}
		} catch(DataAccessException e) {
			log.error("회원정보 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
}
