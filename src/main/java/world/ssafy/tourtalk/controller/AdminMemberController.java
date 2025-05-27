package world.ssafy.tourtalk.controller;

import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.enums.Role;
import world.ssafy.tourtalk.model.dto.request.MemberRequest;
import world.ssafy.tourtalk.model.dto.request.MemberSearchRequest;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.service.AdminMemberService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/members")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMemberController {

	private final AdminMemberService adminMemberService;
	
	@Operation(summary = "관리자 - 회원 목록 조회 (검색 + 페이징)", description = "회원 정보를 검색 조건에 따라 조회하고 페이징 처리합니다.")
	@GetMapping
    public ResponseEntity<PageResponse<MemberResponse>> getMembers(@AuthenticationPrincipal CustomMemberPrincipal principal, @ModelAttribute MemberSearchRequest request) {
			if (principal == null || principal.getRole() != Role.ADMIN) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			try {
				PageResponse<MemberResponse> result = adminMemberService.searchMembers(request);
				return ResponseEntity.ok(result);
			} catch (DataAccessException e) {
				log.error("회원 목록 조회 실패: {}", e.getMessage(), e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}

	@Operation(summary = "관리자 - 회원 상세 조회", description = "회원 번호(mno)를 기준으로 회원 상세 정보를 조회합니다.")
    @GetMapping("/{mno}")
    public ResponseEntity<MemberResponse> getMemberDetail(@PathVariable int mno) {
		try {
			MemberResponse member = adminMemberService.getMemberById(mno);
			return member != null
					? ResponseEntity.ok(member)
					: ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}  catch (DataAccessException e) {
			log.error("회원 상세 조회 실패: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
    }

	@Operation(summary = "관리자 - 회원 정보 수정", description = "회원의 기본 및 상세 정보를 수정합니다. (관리자 계정은 수정 불가)")
    @PutMapping("/{mno}")
    public ResponseEntity<?> updateMember(@PathVariable int mno, @RequestBody MemberRequest request) {
		try {
			adminMemberService.updateMember(mno, request);
			return ResponseEntity.ok().build();
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (IllegalStateException e) {
			// 관리자 계정은 수정 불가
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (DataAccessException e) {
			log.error("회원 수정 실패: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
    }
}
