package world.ssafy.tourtalk.model.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class MemberRequest {
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
	
	// Member
	private int mno;
	private String id;
	private String password;
	private String nickname;
	private Role role;
	private Status status;
	private int points;
	
	//MemberDetails
	private String email;
	private String phone;
    private Gender gender;
	private String address;
	private String postalCode;
	private LocalDate birthDate;
	private String profileImgPath;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime lastLogin;
	
	//Curator
	private String curatorNo;
	private String curatorImg;
	private int adGrade;
	private LocalDateTime approvedAt;
	
	public MemberRequest(int mno, String id, String password, String nickname, Role role, Status status, int points,
			String email, String phone, Gender gender, String address, String postalCode, LocalDate birthDate,
			String profileImgPath, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLogin) {
		this.mno = mno;
		this.id = id;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
		this.status = status;
		this.points = points;
		this.email = email;
		this.phone = phone;
		this.gender = gender;
		this.address = address;
		this.postalCode = postalCode;
		this.birthDate = birthDate;
		this.profileImgPath = profileImgPath;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.lastLogin = lastLogin;
	}

	public MemberRequest(int mno, String id, String password, String nickname, Role role, Status status, int points,
			String email, String phone, Gender gender, String address, String postalCode, LocalDate birthDate,
			String profileImgPath, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLogin,
			String curatorNo, String curatorImg, int adGrade, LocalDateTime approvedAt) {
		super();
		this.mno = mno;
		this.id = id;
		this.password = password;
		this.nickname = nickname;
		this.role = role;
		this.status = status;
		this.points = points;
		this.email = email;
		this.phone = phone;
		this.gender = gender;
		this.address = address;
		this.postalCode = postalCode;
		this.birthDate = birthDate;
		this.profileImgPath = profileImgPath;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.lastLogin = lastLogin;
		this.curatorNo = curatorNo;
		this.curatorImg = curatorImg;
		this.adGrade = adGrade;
		this.approvedAt = approvedAt;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

}
