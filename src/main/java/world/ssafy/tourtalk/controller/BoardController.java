package world.ssafy.tourtalk.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import world.ssafy.tourtalk.model.dto.enums.BoardStatus;
import world.ssafy.tourtalk.model.dto.request.BoardRequest;
import world.ssafy.tourtalk.model.dto.request.SearchConditionRequest;
import world.ssafy.tourtalk.model.dto.response.BoardResponse;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.service.BoardService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

	private final BoardService bService;

	// 게시글 작성
	@PostMapping
	public ResponseEntity<?> write(@RequestBody BoardRequest request) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Integer mno = (Integer) auth.getPrincipal();

			int result = bService.write(request, mno);

			return result > 0 ? ResponseEntity.status(HttpStatus.CREATED).body("게시글 작성 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 작성 실패!!!");
		} catch (DataAccessException e) {
			log.error("게시글 작성 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}

	// 게시글 조회
	@GetMapping("/{postId}")
	public ResponseEntity<?> selectById(@PathVariable int postId) {
		try {
			BoardResponse board = bService.selectById(postId);
			
			if(board.getStatus() == BoardStatus.DELETED) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제된 게시글입니다.");
			
			return board != null ? ResponseEntity.ok(board)
					: ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 존재하지 않습니다.");
		} catch(DataAccessException e) {
			log.error("게시글 작성 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 게시글 수정
	@PutMapping
	public ResponseEntity<?> update(@RequestBody BoardRequest request) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Integer mno = (Integer) auth.getPrincipal();

			int result = bService.update(request, mno);

			return result > 0 ? ResponseEntity.status(HttpStatus.OK).body("게시글 수정 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 수정 실패!!!");
		} catch (DataAccessException e) {
			log.error("게시글 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}

	// 게시글 삭제
	@DeleteMapping("/{postId}")
	public ResponseEntity<?> softDelete(@PathVariable int postId) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Integer mno = (Integer) auth.getPrincipal();

			int result = bService.delete(postId, mno);

			return result > 0 ? ResponseEntity.status(HttpStatus.OK).body("게시글 삭제 성공 !")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("게시글 삭제 실패!!!");
		} catch (DataAccessException e) {
			log.error("게시글 삭제 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}

	
	// 게시글 목록 및 검색
	@GetMapping("/search")
	public ResponseEntity<?> searchOrList( @RequestParam(required = false) String keyword,
	        @RequestParam(required = false) String keywordType,
	        @RequestParam(required = false) Integer categoryId,
	        @RequestParam(required = false) Integer writerId,
	        @RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String orderBy,
	        @RequestParam(required = false) String orderDirection) {
		try {
			SearchConditionRequest condition = SearchConditionRequest.builder()
	                .pageNumber(page)
	                .pageSize(size)
	                .keyword(keyword)
	                .keywordType(keywordType)
	                .categoryId(categoryId != null ? categoryId : 0)
	                .writerId(writerId != null ? writerId : 0)
	                .orderBy(orderBy)
	                .orderDirection(orderDirection)
	                .build();
			
			condition.setDefaults();

            PageResponse<BoardResponse> result = bService.searchWithConditions(condition);

            return result.getContent().isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 존재하지 않습니다.")
                : ResponseEntity.ok(result);
		} catch(DataAccessException e) {
			log.error("게시글 검색 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	// 게시글 목록
	@GetMapping("/list")
	public ResponseEntity<?> selectAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		try {
			SearchConditionRequest condition = SearchConditionRequest.builder()
					.pageNumber(page)
					.pageSize(size)
					.build();
			
			condition.setDefaults();
			
			PageResponse<BoardResponse> result = bService.searchWithConditions(condition);
			
			return result != null ? ResponseEntity.ok(result)
					: ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 존재하지 않습니다.");
		} catch(DataAccessException e) {
			log.error("게시글 목록 조회 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
}
