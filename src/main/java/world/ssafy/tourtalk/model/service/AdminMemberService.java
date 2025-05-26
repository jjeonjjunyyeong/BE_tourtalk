package world.ssafy.tourtalk.model.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.enums.MemberStatus;
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
	    MemberResponse existing = memberMapper.findMemberDetailById(mno)
	            .orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을 수 없습니다."));

	    if (existing.getRole() == Role.ADMIN) {
	        throw new IllegalStateException("관리자 계정은 수정할 수 없습니다.");
	    }

	    // 공통 정보 병합
	    MemberRequest merged = MemberRequest.builder()
	            .mno(mno)
	            .nickname(request.getNickname() != null ? request.getNickname() : existing.getNickname())
	            .role(request.getRole() != null ? request.getRole() : existing.getRole())
	            .status(request.getStatus() != null ? request.getStatus() : existing.getStatus())
	            .email(request.getEmail() != null ? request.getEmail() : existing.getEmail())
	            .phone(request.getPhone() != null ? request.getPhone() : existing.getPhone())
	            .gender(request.getGender() != null ? request.getGender() : existing.getGender())
	            .address(request.getAddress() != null ? request.getAddress() : existing.getAddress())
	            .postalCode(request.getPostalCode() != null ? request.getPostalCode() : existing.getPostalCode())
	            .birthDate(request.getBirthDate() != null ? request.getBirthDate() : existing.getBirthDate())
	            .profileImgPath(request.getProfileImgPath() != null ? request.getProfileImgPath() : existing.getProfileImgPath())
	            .build();

	    // member + member_details 업데이트
	    memberMapper.updateByAdmin(mno, merged.getNickname(), merged.getRole(), merged.getStatus());
	    memberMapper.updateDetails(merged);

	    // 큐레이터일 경우 curator 필드 따로 병합해서 업데이트
	    if (existing.getRole() == Role.CURATOR) {
	        MemberRequest curatorPart = MemberRequest.builder()
	                .mno(mno)
	                .curatorNo(request.getCuratorNo() != null ? request.getCuratorNo() : existing.getCuratorNo())
	                .curatorImg(request.getCuratorImg() != null ? request.getCuratorImg() : existing.getCuratorImg())
	                .adGrade(request.getAdGrade() != 0 ? request.getAdGrade() : existing.getAdGrade()) // 기본값 0 대신 기존 값 사용
	                .approvedAt(request.getApprovedAt() != null ? request.getApprovedAt() : existing.getApprovedAt())
	                .build();

	        memberMapper.updateCurator(curatorPart);
	    }
	}

}
