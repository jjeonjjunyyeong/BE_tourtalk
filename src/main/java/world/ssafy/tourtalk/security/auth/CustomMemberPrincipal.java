package world.ssafy.tourtalk.security.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import world.ssafy.tourtalk.model.dto.enums.Role;

@Getter
@AllArgsConstructor
public class CustomMemberPrincipal {
	private final int mno;
	private final String id;
	private final String nickname;
	private final Role role;
}
