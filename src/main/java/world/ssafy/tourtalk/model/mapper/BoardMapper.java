package world.ssafy.tourtalk.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import world.ssafy.tourtalk.model.dto.Board;
import world.ssafy.tourtalk.model.dto.BoardDetails;

@Mapper
public interface BoardMapper {

	int writeBoard(Board board);

	int writeBoardDetails(BoardDetails details);

	int updateBoard(Board board);

	int updateBoardDetails(BoardDetails details);

	Board findById(int postId);

	int softDelete(int postId);

}
