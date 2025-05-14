package world.ssafy.tourtalk.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.enums.MemberStatus;
import world.ssafy.tourtalk.model.dto.request.MemberRequest;
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
	
	int update(MemberRequest request);
	int updateDetails(MemberRequest request);
	int updateCurator(MemberRequest corrected);

	int softDelete(@Param("mno") Integer mno, @Param("status") MemberStatus deleted);
}
