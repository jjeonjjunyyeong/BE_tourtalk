package world.ssafy.tourtalk.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.enums.BoardStatus;
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
	int softDelete(@Param("postId") int postId, @Param("status") BoardStatus deleted);

	int softDeleteDetail(@Param("postId") int postId);

	// 게시글 전체 조회
	List<BoardResponse> selectAll(@Param("cond") SearchConditionRequest cond, @Param("offset") int offset,
			@Param("limit") int limit);

	long countAll(@Param("cond") SearchConditionRequest cond);

	// 게시글 검색
	List<BoardResponse> searchWithConditions(@Param("cond") SearchConditionRequest cond, @Param("offset") int offset,
			@Param("pageSize") int pageSize);

	long countWithConditions(@Param("cond") SearchConditionRequest cond);

	// 게시글에 댓글 생성
	boolean updateCommentCount(@Param("postId") int postId);

	// 마이페이지 : 내가 작성한 게시글 조회
	List<BoardResponse> selectMyPosts(@Param("cond") SearchConditionRequest cond,
            @Param("offset") int offset,
            @Param("size") int size);
	int countMyPosts(@Param("cond") SearchConditionRequest cond);
}
