package world.ssafy.tourtalk.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.request.BoardRequest;
import world.ssafy.tourtalk.model.dto.request.SearchConditionRequest;
import world.ssafy.tourtalk.model.dto.response.BoardResponse;

@Mapper
public interface BoardMapper {

	// 게시글 작성
	int writeBoard(BoardRequest board);
	int writeBoardDetails(BoardRequest details);

	// 게시글 수정
	int updateBoard(BoardRequest board);
	int updateBoardDetails(BoardRequest details);

	// 게시글 조회
	BoardResponse selectById(int postId);
	// 조회수 증가
	void updateViewCount(int postId);

	// 게시글ID 기반 찾기
	BoardResponse findById(int postId);

	// 게시글 삭제
	int softDelete(BoardRequest request);

	List<BoardResponse> searchWithConditions(@Param("cond") SearchConditionRequest cond, @Param("offset") int offset,
			@Param("pageSize") int pageSize);

	long countWithConditions(@Param("cond") SearchConditionRequest cond);



}
