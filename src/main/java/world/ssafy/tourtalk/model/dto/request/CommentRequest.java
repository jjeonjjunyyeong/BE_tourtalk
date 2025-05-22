package world.ssafy.tourtalk.model.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.CommentStatus;

@Getter
@NoArgsConstructor
@Builder
public class CommentRequest {
	private int commentId;
	private int postId;
	private int writerId;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private CommentStatus status;
	
	public CommentRequest(int commentId, int postId, int writerId, String content, LocalDateTime createdAt,
			LocalDateTime updatedAt, CommentStatus status) {
		super();
		this.commentId = commentId;
		this.postId = postId;
		this.writerId = writerId;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.status = status;
	}
}
