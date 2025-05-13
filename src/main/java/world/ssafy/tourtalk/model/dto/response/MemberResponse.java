package world.ssafy.tourtalk.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.Member;
import world.ssafy.tourtalk.model.dto.MemberDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {
	private Member member;
	private MemberDetails memberDetails;
}
