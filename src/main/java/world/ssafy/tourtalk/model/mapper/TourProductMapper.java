package world.ssafy.tourtalk.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.request.TourProductRequest;
import world.ssafy.tourtalk.model.dto.response.TourProductResponse;

@Mapper
public interface TourProductMapper {

	int insert(@Param("request") TourProductRequest request, @Param("mno") int mno);

	TourProductResponse getById(@Param("productId") int productId);

	int update(@Param("productId") int productId,
	           @Param("request") TourProductRequest request,
	           @Param("mno") int mno);

	int softDeleted(@Param("productId") int productId);

}