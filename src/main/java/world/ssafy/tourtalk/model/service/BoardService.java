package world.ssafy.tourtalk.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.request.BoardRequest;
import world.ssafy.tourtalk.model.dto.request.SearchConditionRequest;
import world.ssafy.tourtalk.model.dto.response.BoardResponse;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.mapper.BoardMapper;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardMapper boardMapper;
	
	@Transactional
	public int write(BoardRequest request, Integer mno) {
		//if (mno != null && mno.equals(request.getWriterId())) return boardMapper.write(request);
		return 0;
	}

	@Transactional
	public BoardResponse selectById(int postId) {
		boardMapper.updateViewCount(postId);
		return boardMapper.selectById(postId);
	}

	@Transactional
	public int update(BoardRequest request, Integer mno) {
		//if (mno != null && mno.equals(request.getWriterId())) return boardMapper.update(request);
		return 0;
	}

	@Transactional
	public int delete(int postId, Integer mno) {
		//BoardResponse board = boardMapper.findById(postId);

		return 0;
	}

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
