package world.ssafy.tourtalk.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comments {
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
}
