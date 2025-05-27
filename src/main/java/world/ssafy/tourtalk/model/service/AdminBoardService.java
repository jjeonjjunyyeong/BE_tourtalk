package world.ssafy.tourtalk.model.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.enums.BoardCategory;
import world.ssafy.tourtalk.model.dto.enums.BoardStatus;
import world.ssafy.tourtalk.model.dto.request.BoardRequest;
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

	// 관리자 - 게시글 수정
	@Transactional
	public boolean updateBoardByAdmin(int postId, BoardRequest request) {
		BoardResponse current = boardMapper.selectById(postId);

		if (current == null) {
			throw new NoSuchElementException("해당 게시글이 존재하지 않습니다.");
		}

		String newTitle = request.getTitle() != null ? request.getTitle() : current.getTitle();
		BoardCategory newCategory = request.getCategory() != null ? request.getCategory() : current.getCategory();
		BoardStatus newStatus = request.getStatus() != null ? request.getStatus() : current.getStatus();
		return boardMapper.updateBoardByAdmin(postId, newTitle, newCategory, newStatus) > 0;
	}

	// 관리자 - 게시글 조회
	public BoardResponse getBoardDetail(int postId) {
		return boardMapper.selectById(postId);
	}

}
