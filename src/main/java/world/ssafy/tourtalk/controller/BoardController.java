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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.request.BoardUpdateRequest;
import world.ssafy.tourtalk.model.dto.request.BoardWriteRequest;
import world.ssafy.tourtalk.model.service.BoardService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
public class BoardController {

	private final BoardService bService;
	
	@PostMapping
	public ResponseEntity<?> write(@RequestBody BoardWriteRequest request) {
		try {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        Integer mno = (Integer) auth.getPrincipal();
	        
			int result = bService.write(request, mno);
			
			return result > 0 ? ResponseEntity.status(HttpStatus.CREATED).body("게시글 작성 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 작성 실패!!!");			
		} catch(DataAccessException e) {
			log.error("게시글 작성 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	@PutMapping
	public ResponseEntity<?> update(@RequestBody BoardUpdateRequest request) {
		try {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        Integer mno = (Integer) auth.getPrincipal();
	        
			int result = bService.update(request, mno);
			
			return result > 0 ? ResponseEntity.status(HttpStatus.OK).body("게시글 수정 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 수정 실패!!!");			
		} catch(DataAccessException e) {
			log.error("게시글 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	@DeleteMapping("/{postId}")
	public ResponseEntity<?> delete(@PathVariable int postId) {
		try {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        Integer mno = (Integer) auth.getPrincipal();
			
			int result = bService.delete(postId, mno);
			
			return result > 0 ? ResponseEntity.status(HttpStatus.OK).body("게시글 삭제 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 삭제 실패!!!");			
		} catch(DataAccessException e) {
			log.error("게시글 삭제 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
}
