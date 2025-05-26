package world.ssafy.tourtalk.model.mapper;

import java.time.LocalTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.request.ProductSearchRequest;
import world.ssafy.tourtalk.model.dto.request.TourProductRequest;
import world.ssafy.tourtalk.model.dto.response.TourProductResponse;

@Mapper
public interface TourProductMapper {

	// 상품 등록
	int insertProduct(@Param("request") TourProductRequest request);
	int insertTimeSlot(@Param("productId") int productId, @Param("time") String time);

	// 상품 상세 조회
	TourProductResponse getById(@Param("productId") int productId);

	int update(@Param("productId") int productId,
	           @Param("request") TourProductRequest request,
	           @Param("mno") int mno);

	int softDeleted(@Param("productId") int productId);
	
	// 특정 큐레이터의 상품 전체 조회
	List<TourProductResponse> findByCurator(int mno);
	
	// 상품 시간대 조회
	List<LocalTime> selectTimeSlots(@Param("productId") int productId);
	
	// 상품 예약 가능 최대 인원 조회
	int getMaxParticipants(int productId);
	
	// 조건에 맞는 예약 가능한 상품의 총 개수를 조회
	int countAvailableProducts(ProductSearchRequest condition);
	// 조건에 맞는 예약 가능한 상품 목록을 페이징하여 조회
	List<TourProductResponse> findAvailableProducts(ProductSearchRequest condition);

	// locationNo로 locationName 가져오기
	String getLocationById(int locationNo);
	
	
}
