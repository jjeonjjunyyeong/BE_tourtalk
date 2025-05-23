package world.ssafy.tourtalk.controller;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.request.CommentRequest;
import world.ssafy.tourtalk.model.dto.request.SearchConditionRequest;
import world.ssafy.tourtalk.model.dto.response.CommentResponse;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.service.CommentService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

	private final CommentService cService;
	
	//댓글 작성
	@PostMapping
	public ResponseEntity<?> write(@AuthenticationPrincipal CustomMemberPrincipal principal, @RequestBody CommentRequest request) {
		try {
			if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	        
			boolean result = cService.write(request, principal.getMno());
			
			return result 
					? ResponseEntity.status(HttpStatus.CREATED).body("댓글 작성 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 작성 실패!!!");			
		} catch(DataAccessException e) {
			log.error("댓글 작성 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 댓글 수정
	@PutMapping
	public ResponseEntity<?> update(@AuthenticationPrincipal CustomMemberPrincipal principal, @RequestBody CommentRequest request) {
		try {
			boolean result = cService.update(request, principal.getMno());;

			return result
					? ResponseEntity.status(HttpStatus.OK).body("댓글 수정 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 수정 실패!!!");			
		} catch(DataAccessException e) {
			log.error("댓글 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	//댓글 삭제
	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> softDelete(@AuthenticationPrincipal CustomMemberPrincipal principal, @PathVariable int commentId) {
		try {
			boolean result = false;
			if(cService.selectByWriterId(commentId) == principal.getMno()) {
				result = cService.softDelete(commentId);				
			}
			
			return result 
					? ResponseEntity.status(HttpStatus.OK).body("댓글 삭제 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 삭제 실패!!!");			
		} catch(DataAccessException e) {
			log.error("댓글 삭제 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 게시글 댓글 조회
	@GetMapping
	public ResponseEntity<?> selectAllByPostId(@RequestParam int postId) {
		try {
			List<CommentResponse> comments = cService.selectAllByPostId(postId);
			return comments != null
					? ResponseEntity.ok(comments)
					: ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글이 아직 작성되지 않았습니다.");
		} catch(DataAccessException e) {
			log.error("댓글 삭제 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	
	// 마이페이지 : 작성자 댓글 전체 조회
	@GetMapping("/myComments")
	public ResponseEntity<?> getMyPosts(@RequestParam int writerId, @RequestParam(name = "pageNumber", defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int size) {
		try {
			SearchConditionRequest condition = SearchConditionRequest.builder()
					.pageNumber(page)
					.pageSize(size)
					.writerId(writerId)
					.build();
		
			PageResponse<CommentResponse> result = cService.getMyComments(condition);
			return result != null
					? ResponseEntity.ok(result)
					: ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글이 존재하지 않습니다.");
		} catch(DataAccessException e) {
			log.error("게시글 목록 조회 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
}
