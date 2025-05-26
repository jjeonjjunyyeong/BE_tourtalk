package world.ssafy.tourtalk.model.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.enums.Role;
import world.ssafy.tourtalk.model.dto.request.MemberRequest;
import world.ssafy.tourtalk.model.dto.request.MemberSearchRequest;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.mapper.MemberMapper;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

	private final MemberMapper memberMapper;

	// 회원 목록 검색 + 페이징
	public PageResponse<MemberResponse> searchMembers(MemberSearchRequest request) {
		request.setDefaults();
		int offset = request.getOffset();

		// 데이터 조회
		List<MemberResponse> list = memberMapper.findMembersWithCondition(request, offset);
		int total = memberMapper.countMembersWithCondition(request);

		// 페이징 계산
		int totalPages = (int) Math.ceil((double) total / request.getPageSize());
		boolean first = request.getPageNumber() == 1;
		boolean last = request.getPageNumber() >= totalPages;

		// Page 객체 생성 및 페이지 정보 계산
		Page<MemberResponse> page = new Page<>(list, request.getPageNumber(), request.getPageSize(), total, totalPages,
				first, last);
		page.calculatePageInfo(5); // 네비게이션 블록 크기 설정

		// PageResponse 변환 및 반환
		return PageResponse.from(page);
	}

	// 회원 상세 조회
	public MemberResponse getMemberById(int mno) {
		return memberMapper.findMemberDetailById(mno)
				.orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을 수 없습니다."));
	}

	// 회원 정보 수정 (USER, CURATOR)
	public void updateMember(int mno, MemberRequest request) {
		// 기본 회원 확인
		MemberResponse existing = memberMapper.findMemberDetailById(mno)
				.orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을 수 없습니다."));

		if (existing.getRole() == Role.ADMIN) {
			throw new IllegalStateException("관리자 계정은 수정할 수 없습니다.");
		}

		request.setMno(mno);
		
		// member + member_details 업데이트
		memberMapper.updateByAdmin(mno, request);
		memberMapper.updateDetails(request);

		// 큐레이터일 경우 추가 정보도 업데이트
		if (existing.getRole() == Role.CURATOR && request.getCuratorNo() != null) {
			memberMapper.updateCurator(request);
		}
	}
}
