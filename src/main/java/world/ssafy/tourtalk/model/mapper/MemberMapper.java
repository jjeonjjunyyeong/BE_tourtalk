package world.ssafy.tourtalk.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.enums.MemberStatus;
import world.ssafy.tourtalk.model.dto.request.MemberRequest;
import world.ssafy.tourtalk.model.dto.response.MemberResponse;

@Mapper
public interface MemberMapper {

	int insertMember(MemberRequest request);
	int insertMemberDetails(MemberRequest request);
	int insertCurator(MemberRequest request);
		
	MemberResponse me(String id);
	MemberResponse getMemberById(@Param("id") String id, @Param("status") MemberStatus status);
	MemberResponse getDetailsByMno(int mno);
	MemberResponse getCuratorByMno(int mno);
	
	int update(MemberRequest request);
	int updateDetails(MemberRequest request);

	int softDelete(@Param("id") String id, @Param("status") MemberStatus status);
}
