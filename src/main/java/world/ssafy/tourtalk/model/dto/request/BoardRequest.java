package world.ssafy.tourtalk.model.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.BoardCategory;
import world.ssafy.tourtalk.model.dto.enums.BoardStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRequest {
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
	
	public BoardRequest(int postId, BoardStatus status) {
		super();
		this.postId = postId;
		this.status = status;
	}
}
