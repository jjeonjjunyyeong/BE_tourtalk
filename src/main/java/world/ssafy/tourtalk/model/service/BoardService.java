package world.ssafy.tourtalk.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.enums.BoardStatus;
import world.ssafy.tourtalk.model.dto.request.BoardRequest;
import world.ssafy.tourtalk.model.dto.request.SearchConditionRequest;
import world.ssafy.tourtalk.model.dto.response.BoardResponse;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.mapper.BoardMapper;

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
	    BoardRequest request = BoardRequest.builder()
	        .postId(postId)
	        .status(BoardStatus.DELETED)
	        .build();

	    return boardMapper.softDelete(request) > 0;
	}
	
	// 게시글ID 기반 찾기
	public BoardResponse findById(int postId) {
		return boardMapper.findById(postId);
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




}
