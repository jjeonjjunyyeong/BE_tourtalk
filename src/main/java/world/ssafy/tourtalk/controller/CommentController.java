package world.ssafy.tourtalk.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.request.CommentUpdateRequest;
import world.ssafy.tourtalk.model.dto.request.CommentWriteRequest;
import world.ssafy.tourtalk.model.service.CommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {

	private final CommentService cService;
	
	@PostMapping
	public ResponseEntity<?> write(@RequestBody CommentWriteRequest request) {
		try {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        Integer mno = (Integer) auth.getPrincipal();
			
			int result = cService.write(request, mno);
			
			return result > 0 ? ResponseEntity.status(HttpStatus.CREATED).body("댓글 작성 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 작성 실패!!!");			
		} catch(DataAccessException e) {
			log.error("댓글 작성 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	@PutMapping
	public ResponseEntity<?> update(@RequestBody CommentUpdateRequest request) {
		try {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        Integer mno = (Integer) auth.getPrincipal();
			
			int result = cService.update(request, mno);
			
			return result > 0 ? ResponseEntity.status(HttpStatus.OK).body("댓글 수정 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 수정 실패!!!");			
		} catch(DataAccessException e) {
			log.error("댓글 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> softDelete(@PathVariable int commentId) {
		try {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        Integer mno = (Integer) auth.getPrincipal();
			
			int result = cService.delete(commentId, mno);
			
			return result > 0 ? ResponseEntity.status(HttpStatus.OK).body("댓글 삭제 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("댓글 삭제 실패!!!");			
		} catch(DataAccessException e) {
			log.error("댓글 삭제 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
}
