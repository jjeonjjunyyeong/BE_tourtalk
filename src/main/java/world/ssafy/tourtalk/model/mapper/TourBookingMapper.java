package world.ssafy.tourtalk.model.mapper;

import java.time.LocalTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.enums.BookingStatus;
import world.ssafy.tourtalk.model.dto.enums.PaymentStatus;
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

	// 현재 로그인한 회원의 예약된 상품 목록 확인
	List<TourBookingResponse> findByMember(int mno);

	// 예약 취소
	int cancelBooking(int bookingId);

	// BookingId를 통해 예약 정보 가져오기
	TourBookingResponse getBookingById(int bookingId);

	// 결제 완료 처리
	int updatePaymentStatus(@Param("bookingId") int bookingId,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("status") BookingStatus status);

}
