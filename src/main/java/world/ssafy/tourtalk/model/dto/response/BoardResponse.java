package world.ssafy.tourtalk.model.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class BoardResponse {
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
	// Board
	private int postId;
	private int categoryId;
	private int writerId;
	private String title;
	private String content;
	private Status status;
	private int viewCount;
	private int commentCount;
	// BoardDetail
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;
	private String filePath;
	
	public BoardResponse(int postId, int categoryId, int writerId, String title, String content, Status status,
			int viewCount, int commentCount, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
			String filePath) {
		this.postId = postId;
		this.categoryId = categoryId;
		this.writerId = writerId;
		this.title = title;
		this.content = content;
		this.status = status;
		this.viewCount = viewCount;
		this.commentCount = commentCount;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
		this.filePath = filePath;
	}
}
