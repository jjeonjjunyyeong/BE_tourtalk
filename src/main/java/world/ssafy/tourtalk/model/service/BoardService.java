package world.ssafy.tourtalk.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.request.BoardRequest;
import world.ssafy.tourtalk.model.dto.response.BoardResponse;
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

	public BoardResponse selectById(int postId) {
		// TODO Auto-generated method stub
		return null;
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


}
