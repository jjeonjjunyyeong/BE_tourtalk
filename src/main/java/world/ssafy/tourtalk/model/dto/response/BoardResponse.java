package world.ssafy.tourtalk.model.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import world.ssafy.tourtalk.model.dto.enums.BoardCategory;
import world.ssafy.tourtalk.model.dto.enums.BoardStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BoardResponse {
	// Board
	private int postId;
	private BoardCategory category;
	private int writerId;
	private String title;
	private String content;
	private BoardStatus status;
	private int viewCount;
	private int commentCount;
	// BoardDetail
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;
	private String filePath;
	// member
	private String writerNickname;
	
	public BoardResponse(int postId, BoardCategory category, int writerId, String title, BoardStatus status, int viewCount,
			int commentCount, LocalDateTime createdAt, LocalDateTime updatedAt, String filePath, String writerNickname) {
		this.postId = postId;
		this.category = category;
		this.writerId = writerId;
		this.title = title;
		this.status = status;
		this.viewCount = viewCount;
		this.commentCount = commentCount;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.filePath = filePath;
		this.writerNickname = writerNickname;
	}

	public BoardResponse(int postId, BoardCategory category, int writerId, String title, String content,
			BoardStatus status, int viewCount, int commentCount, LocalDateTime createdAt, LocalDateTime updatedAt,
			String filePath, String writerNickname) {
		super();
		this.postId = postId;
		this.category = category;
		this.writerId = writerId;
		this.title = title;
		this.content = content;
		this.status = status;
		this.viewCount = viewCount;
		this.commentCount = commentCount;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.filePath = filePath;
		this.writerNickname = writerNickname;
	}
}
