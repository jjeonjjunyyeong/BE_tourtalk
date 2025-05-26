package world.ssafy.tourtalk.model.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.enums.BoardStatus;
import world.ssafy.tourtalk.model.dto.request.BoardSearchRequest;
import world.ssafy.tourtalk.model.dto.response.BoardResponse;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.mapper.BoardMapper;

@Service
@RequiredArgsConstructor
public class AdminBoardService {

	private final BoardMapper boardMapper;

	// 관리자 - 게시글 목록 조회
	public PageResponse<BoardResponse> searchBoards(BoardSearchRequest request) {
		request.setDefaults();
		int offset = request.getOffset();

		// 데이터 조회
		List<BoardResponse> list = boardMapper.findBoardsWithCondition(request, offset);
		int total = boardMapper.countBoardsWithCondition(request);

		int totalPages = (int) Math.ceil((double) total / request.getPageSize());
		boolean first = request.getPageNumber() == 1;
		boolean last = request.getPageNumber() >= totalPages;

		Page<BoardResponse> page = new Page<>(list, request.getPageNumber(), request.getPageSize(), total, totalPages,
				first, last);
		page.calculatePageInfo(5);

		return PageResponse.from(page);
	}

	// 관리자 - 게시글 상태 변경
	public void updateBoardStatus(int postId, BoardStatus status) {
		BoardResponse current = boardMapper.selectById(postId);
		
		if (current == null) {
	        throw new NoSuchElementException("해당 게시글이 존재하지 않습니다.");
	    }

		BoardStatus currentStatus = current.getStatus();

		if (currentStatus == BoardStatus.DELETED) {
			throw new IllegalStateException("삭제된 게시글은 상태를 변경할 수 없습니다.");
		}

		if (currentStatus == status)
			return; // 상태 변경 없음

		boardMapper.updateStatus(postId, status);

	}

	public BoardResponse getBoardDetail(int postId) {
		// TODO Auto-generated method stub
		return null;
	}

}
