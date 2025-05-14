package world.ssafy.tourtalk.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Curator;
import world.ssafy.tourtalk.model.dto.Member;
import world.ssafy.tourtalk.model.dto.MemberDetails;
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
		int memberResult = memberMapper.insertMember(request);
		int memberDetailResult = memberMapper.insertMemberDetails(request);
		
		if (memberResult == 1 && memberDetailResult == 1) {
			if (request.getRole() == MemberRequest.Role.CURATOR) {
				int curatorResult = memberMapper.insertCurator(request);
				return curatorResult == 1;
			}
			return true;
		}
		return false;
	}

	// 회원 정보
	public MemberResponse me(String id) {
		MemberResponse member = memberMapper.getMemberById(id, MemberStatus.DELETED);
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

	// 회원 상세 정보
	public MemberDetails getDetails(int mno) {
		return memberMapper.getDetailsByMno(mno);
	}	
	
	// 회원정보수정
	@Transactional
	public int update(MemberUpdateRequest request) {
		Member member = request.getMember();
		MemberDetails details = request.getMemberDetails();
		
		Member origin = memberMapper.findById(member.getId(), Member.Status.DELETED);
		
	    if (member.getPassword() != null && !member.getPassword().isBlank()) {
	        member.setPassword(passwordEncoder.encode(member.getPassword()));
	    } else {
	        member.setPassword(origin.getPassword());
	    }
	    
	    // 테스트를 위한 코드 추후 삭제
	    if (member.getRole() == null) member.setRole(origin.getRole());
	    if (member.getStatus() == null) member.setStatus(origin.getStatus());
	    if (member.getPoints() == 0) member.setPoints(origin.getPoints());
		
		int result = memberMapper.update(member);
		result += memberMapper.updateDetails(details);
		return result;
	}
	
	// 회원탈퇴
	@Transactional
	public int softDelete(String id) {
		return memberMapper.softDelete(id, Member.Status.DELETED);
	}
}
