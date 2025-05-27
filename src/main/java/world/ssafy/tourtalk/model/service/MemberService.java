package world.ssafy.tourtalk.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.MemberStatus;
import world.ssafy.tourtalk.model.dto.enums.Role;
import world.ssafy.tourtalk.model.dto.request.MemberRequest;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;
import world.ssafy.tourtalk.model.mapper.MemberMapper;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberMapper memberMapper;
	private final BCryptPasswordEncoder passwordEncoder;
	
	// 회원가입
	@Transactional
	public boolean regist(MemberRequest request) {
		request.setPassword(passwordEncoder.encode(request.getPassword()));
		String cleanPhone = request.getPhone().replaceAll("-", "");
	    request.setPhone(cleanPhone);
	    
		int memberResult = memberMapper.insertMember(request);
		int memberDetailResult = memberMapper.insertMemberDetails(request);
		
		if (memberResult == 1 && memberDetailResult == 1) {
			if (request.getRole() == Role.CURATOR) {
				int curatorResult = memberMapper.insertCurator(request);
				return curatorResult == 1;
			}
			return true;
		}
		return false;
	}

	// 회원 정보
	public MemberResponse getDetails(int mno) {
		return memberMapper.getDetailsByMno(mno);
	}	
	
	// 회원 상세 정보
	public MemberResponse me(int mno) {
		MemberResponse member = memberMapper.getMemberByMno(mno, MemberStatus.DELETED);
		if(member == null) return null;
		
		MemberResponse details = memberMapper.getDetailsByMno(member.getMno());
		
		MemberResponse.MemberResponseBuilder builder = MemberResponse.builder()
		        .mno(member.getMno())
		        .id(member.getId())
		        .nickname(member.getNickname())
		        .role(member.getRole())
		        .status(member.getStatus())
		        .points(member.getPoints())
		        .createdAt(member.getCreatedAt())
		        .updatedAt(member.getUpdatedAt())
		        .lastLogin(member.getLastLogin())
		        .email(details.getEmail())
		        .phone(details.getPhone())
		        .gender(details.getGender())
		        .address(details.getAddress())
		        .postalCode(details.getPostalCode())
		        .birthDate(details.getBirthDate())
		        .profileImgPath(details.getProfileImgPath());
		
		 if (member.getRole() == Role.CURATOR) {
		        MemberResponse curator = memberMapper.getCuratorByMno(member.getMno());
		        builder
		            .curatorNo(curator.getCuratorNo())
		            .curatorImg(curator.getCuratorImg())
		            .adGrade(curator.getAdGrade())
		            .approvedAt(curator.getApprovedAt());
		    }
		 
		return builder.build();
	}

	// 회원정보 수정 시 값이 없으면 기존값 사용
	private String nonNullOr(String value, String fallback) {
	    return (value != null && !value.isBlank()) ? value : fallback;
	}
	
	// 회원정보수정
	@Transactional
	public boolean update(MemberRequest request) {
		MemberResponse origin = memberMapper.getMemberByMno(request.getMno(), MemberStatus.DELETED);
		if(origin == null) return false;
		
	    String password = (request.getPassword() != null && !request.getPassword().isBlank())
	            ? passwordEncoder.encode(request.getPassword())
	            : origin.getPassword();
	    
	    boolean isCurator = origin.getRole() == Role.CURATOR;
	    
	    MemberRequest.MemberRequestBuilder builder = MemberRequest.builder()
	            .mno(request.getMno())
	            .id(origin.getId())
	            .password(password)
	            .nickname(nonNullOr(request.getNickname(), origin.getNickname()))
	            .role(origin.getRole())
	            .status(origin.getStatus())
	            .points(origin.getPoints())
	            .email(nonNullOr(request.getEmail(), origin.getEmail()))
	            .phone(nonNullOr(request.getPhone(), origin.getPhone()))
	            .gender(request.getGender() != null ? request.getGender() : origin.getGender())
	            .address(nonNullOr(request.getAddress(), origin.getAddress()))
	            .postalCode(nonNullOr(request.getPostalCode(), origin.getPostalCode()))
	            .birthDate(request.getBirthDate() != null ? request.getBirthDate() : origin.getBirthDate())
	            .profileImgPath(nonNullOr(request.getProfileImgPath(), origin.getProfileImgPath()));

	    if (isCurator) {
	        builder.curatorNo(nonNullOr(request.getCuratorNo(), origin.getCuratorNo()))
	               .curatorImg(nonNullOr(request.getCuratorImg(), origin.getCuratorImg()))
	               .adGrade(origin.getAdGrade())
	               .approvedAt(origin.getApprovedAt());
	    }

	    MemberRequest corrected = builder.build();

	    int memberResult = memberMapper.update(corrected);
	    int memberDetailResult = memberMapper.updateDetails(corrected);

		if (memberResult == 1 && memberDetailResult == 1) {
			if (origin.getRole() == Role.CURATOR) {
				int curatorResult = memberMapper.updateCurator(corrected);
				return curatorResult == 1;
			}
			return true;
		}
		return false;
	}
	
	// 회원탈퇴
	@Transactional
	public boolean softDelete(Integer mno, String password) {
		MemberResponse member = memberMapper.selectMemberByMno(mno);
	    if (member == null) return false;

	    // 기존 암호화된 비밀번호와 평문 비교
	    if (!passwordEncoder.matches(password, member.getPassword())) {
	        return false;
	    }
		
		return memberMapper.softDelete(mno, MemberStatus.DELETED) == 1;
	}

	// ID 중복 체크
	public boolean existsById(String id) {
		return memberMapper.existsById(id);
	}

	// 프로필 이미지 업데이트
	@Transactional
	public boolean updateProfileImgPath(int mno, String profileImgPath) {
		return memberMapper.updateProfileImgPath(mno, profileImgPath) > 0;
	}

	// mno로 닉네임 찾기
	public String findNicknameByMno(int mno) {
		return memberMapper.selectNicknameByMno(mno);
	}
}
