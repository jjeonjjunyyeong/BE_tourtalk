package world.ssafy.tourtalk.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.Board;
import world.ssafy.tourtalk.model.dto.BoardDetails;
import world.ssafy.tourtalk.model.dto.request.BoardRequest;
import world.ssafy.tourtalk.model.dto.request.SearchConditionRequest;
import world.ssafy.tourtalk.model.dto.response.BoardResponse;

@Mapper
public interface BoardMapper {

	int writeBoard(Board board);

	int writeBoardDetails(BoardDetails details);

	int updateBoard(Board board);

	int updateBoardDetails(BoardDetails details);

	Board findById(int postId);

	int softDelete(int postId);

	int write(BoardRequest request);

	int update(BoardRequest request);

	List<BoardResponse> searchWithConditions(@Param("cond") SearchConditionRequest cond, @Param("offset") int offset,
			@Param("pageSize") int pageSize);

	long countWithConditions(@Param("cond") SearchConditionRequest cond);

	BoardResponse selectById(int postId);

	void updateViewCount(int postId);

}
