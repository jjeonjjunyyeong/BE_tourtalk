package world.ssafy.tourtalk.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import world.ssafy.tourtalk.model.dto.request.CommentRequest;
import world.ssafy.tourtalk.model.dto.response.CommentResponse;

@Mapper
public interface CommentMapper {

	// 댓글 생성
	int insert(CommentRequest request);

	// 댓글 수정
	int update(CommentRequest request);

	// 댓글 삭제
	int softDelete(int commentId);

	// 댓글 작성자 mno 조회
	int selectByWriterId(int commentId);

	// 게시글에 작성된 댓글 전체 조회
	List<CommentResponse> selectAllByPostId(int postId);

	// 댓글 id로 정보 조회
	CommentResponse selectByCommentId(int commentId);
}
