package world.ssafy.tourtalk.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.Curator;
import world.ssafy.tourtalk.model.dto.Member;
import world.ssafy.tourtalk.model.dto.MemberDetails;

@Mapper
public interface MemberMapper {

	int insertMember(Member member);
	int insertMemberDetails(MemberDetails details);
	int insertCurator(Curator curator);
	
	Member findById(@Param("id") String id, @Param("status") Member.Status status);
	
	Member me(String id);
	MemberDetails getDetailsByMno(int mno);
	
	int update(Member member);
	int updateDetails(MemberDetails details);

	int softDelete(@Param("id") String id, @Param("status") Member.Status status);
}
