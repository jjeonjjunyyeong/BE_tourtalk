package world.ssafy.tourtalk.model.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class CommentResponse {
	public enum Status {
		ACTIVE("공개"), INACTIVE("비공개"), DELETED("삭제");
		
		private final String desc;

	    Status(String desc) {
	        this.desc = desc;
	    }

	    public String getDescription() {
	        return desc;
	    }
	}
	
	private int commentId;
	private int postId;
	private int writerId;
	private String content;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Status status;
	
	public CommentResponse(int commentId, int postId, int writerId, String content, LocalDateTime createdAt,
			LocalDateTime updatedAt, Status status) {
		this.commentId = commentId;
		this.postId = postId;
		this.writerId = writerId;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.status = status;
	}
}
