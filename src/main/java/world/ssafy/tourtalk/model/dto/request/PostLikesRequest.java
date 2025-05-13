package world.ssafy.tourtalk.model.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikesRequest {
	private int postId;
	private int mno;
	private LocalDateTime createdAt;
}
