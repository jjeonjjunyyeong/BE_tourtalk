package world.ssafy.tourtalk.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Curator;
import world.ssafy.tourtalk.model.dto.Member;
import world.ssafy.tourtalk.model.dto.MemberDetails;
import world.ssafy.tourtalk.model.dto.request.MemberRegistRequest;
import world.ssafy.tourtalk.model.dto.request.MemberUpdateRequest;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;
import world.ssafy.tourtalk.model.mapper.MemberMapper;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberMapper memberMapper;
	private final BCryptPasswordEncoder passwordEncoder;
	
	// 회원가입
	@Transactional
	public int regist(MemberRegistRequest request) {
		Member member = request.getMember();
		MemberDetails details = request.getMemberDetails();
		
		member.setPassword(passwordEncoder.encode(member.getPassword()));
		int result = memberMapper.insertMember(member);
		details.setMno(member.getMno());
		result += memberMapper.insertMemberDetails(details);
		
		if(member.getRole().equals(Member.Role.CURATOR)) {
			Curator curator = request.getCurator();
			curator.setMno(member.getMno());
			result += memberMapper.insertCurator(curator);
		} 
		return result;
	}

	// 로그인
	public Member login(String id, String password) {
		Member member = memberMapper.findById(id, Member.Status.DELETED);
		if(member != null && passwordEncoder.matches(password, member.getPassword())) return member;
		return null;
	}
	
	// 회원 정보
	public MemberResponse me(String id) {
		Member member = memberMapper.me(id);
		if(member == null) return null;
		
		MemberDetails details = memberMapper.getDetailsByMno(member.getMno());
		return MemberResponse.builder().member(member).memberDetails(details).build();
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
	public int delete(String id) {
		return memberMapper.delete(id, Member.Status.DELETED);
	}
}
