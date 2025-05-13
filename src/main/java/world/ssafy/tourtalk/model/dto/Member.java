package world.ssafy.tourtalk.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
	public enum Role {
		USER("일반회원"), CURATOR("학예사"), ADMIN("관리자");
		
		private final String desc;

	    Role(String desc) {
	        this.desc = desc;
	    }

	    public String getDesc() {
	        return desc;
	    }
	}
	
	public enum Status {
		ACTIVE("정상"), SUSPENDED("정지"), PENDING("대기"), DELETED("탈퇴");
		
		private final String desc;

	    Status(String desc) {
	        this.desc = desc;
	    }

	    public String getDescription() {
	        return desc;
	    }
	}
	
	private int mno;
	private String id;
	private String password;
	private String nickname;
	private Role role;
	private Status status;
	private int points;
}
