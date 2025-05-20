package world.ssafy.tourtalk.model.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.Gender;
import world.ssafy.tourtalk.model.dto.enums.MemberStatus;
import world.ssafy.tourtalk.model.dto.enums.Role;

@Getter
@NoArgsConstructor
@Builder
public class MemberResponse {
	// Member
	private int mno;
	private String id;
	private String password;
	private String nickname;
	private Role role;
	private MemberStatus status;
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
	
	public MemberResponse(int mno, String id, String password, String nickname, Role role, MemberStatus status, int points,
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

	public MemberResponse(int mno, String id, String password, String nickname, Role role, MemberStatus status, int points,
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

	
}
