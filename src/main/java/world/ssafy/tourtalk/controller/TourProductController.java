package world.ssafy.tourtalk.controller;

import java.nio.file.AccessDeniedException;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.enums.Role;
import world.ssafy.tourtalk.model.dto.request.TourProductRequest;
import world.ssafy.tourtalk.model.service.MemberService;
import world.ssafy.tourtalk.model.service.TourProductService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tourProduct")
public class TourProductController {

	private final TourProductService tService;
	
	//@PreAuthorize("hasRole('CURATOR')")
	@PostMapping
	public ResponseEntity<?> insert(@AuthenticationPrincipal CustomMemberPrincipal principal, @RequestBody TourProductRequest request) {
		try {
			if (principal == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	        }

			if (principal.getRole() != Role.CURATOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("학예사 계정만 등록할 수 있습니다.");
			}

			boolean result = tService.insert(request, principal.getMno());
			
			return result 
					? ResponseEntity.status(HttpStatus.CREATED).body("상품 등록 성공!")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 등록 실패!");	
						
		} catch(DataAccessException e) {
			log.error("상품 등록 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	@PreAuthorize("hasRole('CURATOR')")
	@PutMapping("/{productId}")
	public ResponseEntity<?> update(@AuthenticationPrincipal CustomMemberPrincipal principal, @PathVariable int productId, @RequestBody TourProductRequest request) {
		try {			
			boolean result = tService.update(productId, request, principal.getMno());
			
			return result
					? ResponseEntity.ok("상품 수정 성공!")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 수정 실패!");

			} catch (NotFoundException e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
			} catch (AccessDeniedException e) {
		        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		    } catch (DataAccessException e) {
				log.error("상품 수정 중 오류 발생", e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
			}
	}
	
	@PreAuthorize("hasRole('CURATOR')")
	@PutMapping("/{productId}/deleted")
	public ResponseEntity<?> softDeleted(@AuthenticationPrincipal CustomMemberPrincipal principal, @PathVariable int productId) {
		try {
			boolean result = tService.softDeleted(productId, principal.getMno());

			return result
				? ResponseEntity.ok("상품이 삭제되었습니다.")
				: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 삭제 실패!");
		} catch (DataAccessException e) {
			log.error("상품 삭제 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
		}
	}
}
