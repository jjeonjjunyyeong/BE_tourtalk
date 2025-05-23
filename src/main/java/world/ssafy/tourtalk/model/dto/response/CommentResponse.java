package world.ssafy.tourtalk.model.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.CommentStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {	
	private int commentId;
	private int postId;
	private int writerId;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private CommentStatus status;
	private String writerNickname;
	
	public CommentResponse(int commentId, int postId, int writerId, String content, LocalDateTime createdAt,
			LocalDateTime updatedAt, CommentStatus status) {
		this.commentId = commentId;
		this.postId = postId;
		this.writerId = writerId;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.status = status;
	}
}
