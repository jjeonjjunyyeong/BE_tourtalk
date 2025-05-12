package world.ssafy.tourtalk.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import world.ssafy.tourtalk.model.dto.Member;

@Mapper
public interface MemberMapper {

	int insert(Member member);

	Member login(String id, String password);

}
