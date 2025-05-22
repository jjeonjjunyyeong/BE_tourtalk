package world.ssafy.tourtalk.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.ssafy.tourtalk.controller.BoardController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.enums.BoardStatus;
import world.ssafy.tourtalk.model.dto.request.BoardRequest;
import world.ssafy.tourtalk.model.dto.request.SearchConditionRequest;
import world.ssafy.tourtalk.model.dto.response.BoardResponse;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.mapper.BoardMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardMapper boardMapper;

	// 게시글 작성
	@Transactional
	public boolean write(BoardRequest request) {
		int boardResult = boardMapper.writeBoard(request);
		
		int detailResult = boardMapper.writeBoardDetails(request);

		return boardResult == 1 && detailResult == 1;
	}

	// 게시글 조회
	@Transactional
	public BoardResponse selectById(int postId) {
		BoardResponse response = boardMapper.selectById(postId);
		if(response.getStatus() != BoardStatus.DELETED) {
			boardMapper.updateViewCount(postId);			
		}
		return boardMapper.selectById(postId);
	}

	// 게시글 수정
	@Transactional
	public boolean update(BoardRequest request) {
		int boardResult = boardMapper.updateBoard(request);
		
		int detailResult = boardMapper.updateBoardDetails(request);
		
		return boardResult == 1 && detailResult == 1;
	}

	// 게시글 삭제
	@Transactional
	public boolean softDelete(int postId) {
		int boardResult = boardMapper.softDelete(postId, BoardStatus.DELETED);
		int detailResult = boardMapper.softDeleteDetail(postId);
		
	    return boardResult == 1 && detailResult == 1;
	}
	
	// 게시글ID 기반 찾기
	public BoardResponse findById(int postId) {
		return boardMapper.findById(postId);
	}

	// 게시글 전체 조회
	public PageResponse<BoardResponse> selectAll(SearchConditionRequest cond) {
	    // 기본값 설정
	    cond.setDefaults();
	    // 페이징 계산
	    int offset = cond.getOffset();

	    // 게시글 목록 조회
	    List<BoardResponse> list = boardMapper.selectAll(cond, offset, cond.getPageSize());

	    // 전체 개수 조회
	    long total = boardMapper.countAll(cond);

	    // 페이징 정보 계산
	    int totalPages = (int) Math.ceil((double) total / cond.getPageSize());
	    boolean first = cond.getPageNumber() == 1;
	    boolean last = cond.getPageNumber() >= totalPages;

	    // Page 객체 생성
	    Page<BoardResponse> page = new Page<>(
	        list,
	        cond.getPageNumber(),
	        cond.getPageSize(),
	        total,
	        totalPages,
	        first,
	        last
	    );
	    // 내비게이션 정보 계산
	    page.calculatePageInfo(5); // navSize = 5
	    // PageResponse 생성 후 반환
	    return PageResponse.from(page);
	}

	// 게시글 검색
	public PageResponse<BoardResponse> searchWithConditions(SearchConditionRequest cond) {
		cond.setDefaults();
		int offset = cond.getOffset();

		List<BoardResponse> list = boardMapper.searchWithConditions(cond, offset, cond.getPageSize());
		long total = boardMapper.countWithConditions(cond);

		int totalPages = (int) Math.ceil((double) total / cond.getPageSize());
		boolean first = cond.getPageNumber() == 1;
		boolean last = cond.getPageNumber() >= totalPages;

		Page<BoardResponse> page = new Page<>(list, cond.getPageNumber(), cond.getPageSize(), total, totalPages, first, last);
		page.calculatePageInfo(5); 
		return PageResponse.from(page);
	}

	// 마이페이지 : 작성자 게시글 조회
	public PageResponse<BoardResponse> getMyPosts(SearchConditionRequest cond) {
	    cond.setDefaults(); // pageNumber, pageSize 기본값 설정
	    int offset = cond.getOffset();

	    // 목록 조회
	    List<BoardResponse> list = boardMapper.selectMyPosts(cond, offset, cond.getPageSize());
	    
	    // 총 개수
	    long total = boardMapper.countMyPosts(cond);

	    // 페이징 정보 계산
	    int totalPages = (int) Math.ceil((double) total / cond.getPageSize());
	    boolean first = cond.getPageNumber() == 1;
	    boolean last = cond.getPageNumber() >= totalPages;

	    Page<BoardResponse> page = new Page<>(list, cond.getPageNumber(), cond.getPageSize(), total, totalPages, first, last);
	    page.calculatePageInfo(5); // 페이지네이션 블록 수

	    return PageResponse.from(page);
	}



}
