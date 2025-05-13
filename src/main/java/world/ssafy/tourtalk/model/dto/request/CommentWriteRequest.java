package world.ssafy.tourtalk.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.Comments;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentWriteRequest {
	private Comments comment;
}
