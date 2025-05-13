package world.ssafy.tourtalk.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.Curator;
import world.ssafy.tourtalk.model.dto.Member;
import world.ssafy.tourtalk.model.dto.MemberDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRegistRequest {
	private Member member;
	private MemberDetails memberDetails;
	private Curator curator;
	
	public MemberRegistRequest(Member member, MemberDetails memberDetails) {
		this.member = member;
		this.memberDetails = memberDetails;
	}
}
