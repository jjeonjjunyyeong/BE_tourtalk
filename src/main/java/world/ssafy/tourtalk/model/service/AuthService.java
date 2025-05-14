package world.ssafy.tourtalk.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.MemberStatus;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;
import world.ssafy.tourtalk.model.mapper.MemberMapper;

@Service
@RequiredArgsConstructor
public class AuthService {
	
    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder passwordEncoder;
	
	public MemberResponse login(String id, String password) {
		MemberResponse member = memberMapper.getMemberById(id, MemberStatus.DELETED);
		if(member != null && passwordEncoder.matches(password, member.getPassword())) return member;
		return null;
	}


	
}
