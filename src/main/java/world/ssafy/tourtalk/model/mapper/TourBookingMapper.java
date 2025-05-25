package world.ssafy.tourtalk.model.mapper;

import java.time.LocalTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import world.ssafy.tourtalk.model.dto.request.TourBookingRequest;
import world.ssafy.tourtalk.model.dto.response.TourBookingResponse;

@Mapper
public interface TourBookingMapper {

	// 상품 예약
	int insert(TourBookingRequest booking);
	
	// 예약할 시간에 인원이 몇명이나 예약됐는지 확인
	int countParticipantsByProductAndTime(int productId, LocalTime time);

	// 특정 상품의 시간대별 예약 인원 집계
	List<TourBookingResponse> getBookingCountByProduct(int productId);



}
