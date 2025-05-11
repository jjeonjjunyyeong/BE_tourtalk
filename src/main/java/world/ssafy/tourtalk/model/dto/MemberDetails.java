package world.ssafy.tourtalk.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDetails {
	public enum Gender{
		UNKNOWN("비공개"), MAN("남자"), WOMAN("여자");
		
		private final String desc;

	    Gender(String desc) {
	        this.desc = desc;
	    }

	    public String getDesc() {
	        return desc;
	    }
	}
	
	private int mno;
	private String email;
	private String phone;
	@Builder.Default
    private Gender gender = Gender.UNKNOWN;
	private String address;
	private String postalCode;
	private LocalDate birthDate;
	private String profileImgPath;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime lastLogin;
}
