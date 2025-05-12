package world.ssafy.tourtalk.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Member;
import world.ssafy.tourtalk.model.mapper.MemberMapper;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberMapper memberMapper;
	private BCryptPasswordEncoder passwordEncoder;
	
	// 회원가입
	public int regist(Member member) {
		member.setPassword(passwordEncoder.encode(member.getPassword()));
		return memberMapper.insert(member);
	}

	// 로그인
	public Member login(String id, String password) {
		password = passwordEncoder.encode(password);
		return memberMapper.login(id, password);
	}

	public int modify(Member member) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Member memberInfo(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int delete(String id) {
		// TODO Auto-generated method stub
		return 0;
	}


	
}
