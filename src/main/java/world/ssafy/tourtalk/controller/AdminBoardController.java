package world.ssafy.tourtalk.controller;

import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.enums.Role;
import world.ssafy.tourtalk.model.dto.request.BoardRequest;
import world.ssafy.tourtalk.model.dto.request.BoardSearchRequest;
import world.ssafy.tourtalk.model.dto.response.BoardResponse;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.service.AdminBoardService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/boards")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBoardController {

	private final AdminBoardService adminBoardService;
	
	 @Operation(summary = "관리자 - 게시글 목록 조회", description = "카테고리, 상태, 키워드 조건에 따라 게시글을 검색 및 페이징 조회합니다.")
	    @GetMapping
	    public ResponseEntity<PageResponse<BoardResponse>> getBoards(
	            @AuthenticationPrincipal CustomMemberPrincipal principal,
	            @ModelAttribute BoardSearchRequest request) {
	        
	        if (principal == null || principal.getRole() != Role.ADMIN) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	        }

	        try {
	            PageResponse<BoardResponse> result = adminBoardService.searchBoards(request);
	            return ResponseEntity.ok(result);
	        } catch (DataAccessException e) {
	            log.error("게시글 목록 조회 실패: {}", e.getMessage(), e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }

	 @Operation(summary = "관리자 - 게시글 수정", description = "게시글 ID를 기준으로 카테고리, 제목, 상태를 변경합니다.")
	 @PatchMapping("/{postId}/status")
	 public ResponseEntity<?> updateBoardStatus(@PathVariable int postId, @RequestBody BoardRequest request) {
	     try {
	         adminBoardService.updateBoardStatus(postId, request.getStatus());
	         return ResponseEntity.ok().build();
	     } catch (NoSuchElementException e) {
	         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	     } catch (IllegalStateException e) {
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	     } catch (DataAccessException e) {
	         log.error("게시글 상태 변경 실패: {}", e.getMessage(), e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	     }
	 }
	 
	 @Operation(summary = "관리자 - 게시글 상세 조회", description = "게시글 ID를 기준으로 상세 정보를 조회합니다.")
	 @GetMapping("/admin/boards/{postId}")
	 public ResponseEntity<?> getBoardDetail(@PathVariable int postId) {
	     try {
	         BoardResponse detail = adminBoardService.getBoardDetail(postId);
	         return ResponseEntity.ok(detail);
	     } catch (NoSuchElementException e) {
	         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
	     }
	 }

}
