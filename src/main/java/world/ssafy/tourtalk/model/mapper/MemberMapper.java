package world.ssafy.tourtalk.model.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.enums.MemberStatus;
import world.ssafy.tourtalk.model.dto.request.MemberRequest;
import world.ssafy.tourtalk.model.dto.request.MemberSearchRequest;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;

@Mapper
public interface MemberMapper {

	// 회원가입
	int insertMember(MemberRequest request);
	int insertMemberDetails(MemberRequest request);
	int insertCurator(MemberRequest request);
		
	MemberResponse getMemberById(@Param("id") String id, @Param("status") MemberStatus deleted);
	MemberResponse getMemberByMno(@Param("mno") int mno, @Param("status") MemberStatus status);
	MemberResponse getDetailsByMno(@Param("mno") int mno);
	MemberResponse getCuratorByMno(@Param("mno") int mno);
	
	//회원정보 수정
	int update(MemberRequest request);
	int updateDetails(MemberRequest request);
	int updateCurator(MemberRequest corrected);

	int softDelete(@Param("mno") Integer mno, @Param("status") MemberStatus deleted);
	
	boolean existsById(String id);
	
	int updateProfileImgPath(int mno, String profileImgPath);
	// mno로 회원 조회
	MemberResponse selectMemberByMno(Integer mno);
	
	// mno로 닉네임 찾기
	String selectNicknameByMno(int mno);
	
	// 관리자 페이지 - 검색 조건에 따라 회원 목록을 조회
	List<MemberResponse> findMembersWithCondition(@Param("request") MemberSearchRequest request,
            @Param("offset") int offset);

	// 관리자 페이지 - 검색 조건에 맞는 전체 회원 수를 조회
	int countMembersWithCondition(@Param("request") MemberSearchRequest request);
	
	// 특정 회원의 기본 정보, 상세 정보, 큐레이터 정보를 모두 조회
	Optional<MemberResponse> findMemberDetailById(@Param("mno") int mno);
	
	// 관리자 계정으로 회원정보 수정
	int updateByAdmin(@Param("mno") int mno, @Param("request") MemberRequest request);

}
