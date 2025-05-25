package world.ssafy.tourtalk.controller;

import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	private final MemberService memberService;

	// 회원가입
	@PostMapping
	public ResponseEntity<?> regist(@RequestBody MemberRequest request) {
		try {
			boolean success = memberService.regist(request);
			
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
			MemberResponse response = memberService.me(mno);
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
			boolean success = memberService.update(request);
			
			return success
					? ResponseEntity.status(HttpStatus.OK).body("회원정보 수정 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원정보 수정 실패!!!");
		} catch (DataAccessException e) {
			log.error("회원정보 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 회원 탈퇴
	@PostMapping("/deleted")
	public ResponseEntity<?> softDelete(@AuthenticationPrincipal CustomMemberPrincipal principal, @RequestBody MemberRequest request) {
		try {
			boolean success = memberService.softDelete(request.getMno(), request.getPassword());
			
			if (success) {
				return ResponseEntity.status(HttpStatus.OK).body("회원탈퇴 성공 !");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원탈퇴 실패!!!");
			}
		} catch(DataAccessException e) {
			log.error("회원정보 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 아이디 중복 체크
	@GetMapping("/checkId")
	public ResponseEntity<Map<String, Boolean>> checkId(@RequestParam String id) {
	    boolean available = !memberService.existsById(id);
	    return ResponseEntity.ok(Map.of("available", available));
	}

	// 프로필 이미지 업로드
	@PutMapping("/profile-img")
	public ResponseEntity<?> updateProfileImg(@AuthenticationPrincipal CustomMemberPrincipal principal, @RequestParam String profileImgPath) {
		try {
			boolean result = memberService.updateProfileImgPath(principal.getMno(), profileImgPath);
			
			return result
					? ResponseEntity.status(HttpStatus.CREATED).body("프로필 업로드 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("프로필 업로드 실패!");
		} catch(DataAccessException e) {
			log.error("회원 프로필 업로드 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	@GetMapping("/{mno}/nickname")
	public ResponseEntity<?> getNickname(@PathVariable int mno) {
		try {
			String nickname = memberService.findNicknameByMno(mno);
			return nickname != null
					? ResponseEntity.ok(Map.of("nickname", nickname))
					: ResponseEntity.status(HttpStatus.NOT_FOUND).body("닉네임을 찾을 수 없습니다.");			
		} catch(DataAccessException e) {
			log.error("회원 닉네임 조회 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
}
