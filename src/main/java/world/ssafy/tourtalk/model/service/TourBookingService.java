package world.ssafy.tourtalk.model.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.BookingStatus;
import world.ssafy.tourtalk.model.dto.enums.PaymentStatus;
import world.ssafy.tourtalk.model.dto.request.TourBookingRequest;
import world.ssafy.tourtalk.model.dto.response.TourBookingResponse;
import world.ssafy.tourtalk.model.mapper.TourBookingMapper;
import world.ssafy.tourtalk.model.mapper.TourProductMapper;

@Service
@RequiredArgsConstructor
public class TourBookingService {

	private final TourBookingMapper bookingMapper;
	private final TourProductMapper productMapper;

	// 상품 예약
	public boolean reserve(int mno, TourBookingRequest request) {
		// 현재 상품에 예약되어 있는 인원 확인
		int current = bookingMapper.countParticipantsByProductAndTime(request.getProductId(), request.getTime());
		// 상품 예약 최대 인원 확인
		int max = productMapper.getMaxParticipants(request.getProductId());

		if (current + request.getParticipantCount() > max) {
			throw new IllegalStateException("해당 시간대에 예약 가능한 인원이 부족합니다.");
		}

		// 예약 테이블에 insert
		TourBookingRequest booking = TourBookingRequest.builder()
				.mno(mno)
				.productId(request.getProductId())
				.time(request.getTime())
				.participantCount(request.getParticipantCount())
				.totalPrice(request.getTotalPrice())
				.paymentMethod(request.getPaymentMethod())
				.paymentStatus(request.getPaymentStatus())
				.status(request.getStatus())
				.build();

		return bookingMapper.insert(booking) > 0;
	}
	
	// 상품 별 예약된 인원 수 확인
	public List<TourBookingResponse> getBookingCounts(int productId) {
		// 1. 전체 시간대 조회 (product_time_slot 테이블 기준)
		List<LocalTime> allTimeSlots = productMapper.selectTimeSlots(productId);

		// 2. 예약된 시간대별 인원수 조회 (tour_booking 테이블 기준)
		List<TourBookingResponse> reservedCounts = bookingMapper.getBookingCountByProduct(productId);

		// 3. Map<시간, 인원수>로 변환
		Map<LocalTime, Integer> reservedMap = reservedCounts.stream()
				.collect(Collectors.toMap(TourBookingResponse::getTime, TourBookingResponse::getParticipantCount));

		// 4. 전체 시간대 기준으로 participantCount가 없으면 0으로 설정
		return allTimeSlots.stream().map(time -> new TourBookingResponse(time, reservedMap.getOrDefault(time, 0)))
				.collect(Collectors.toList());
	}

	// 현재 로그인한 회원의 예약된 상품 목록 확인
	public List<TourBookingResponse> getBookingsByMember(int mno) {
		 return bookingMapper.findByMember(mno);
	}

	// 예약 취소
	public boolean cancelBooking(int bookingId, int mno) {
		TourBookingResponse booking = bookingMapper.getBookingById(bookingId);
	    if (booking == null || booking.getMno() != mno) {
	        throw new IllegalArgumentException("권한이 없거나 존재하지 않는 예약입니다.");
	    }
	    return bookingMapper.cancelBooking(bookingId) > 0;
	}

	// 결제 완료 처리
	public void confirmPayment(int bookingId, int mno) throws AccessDeniedException {
		TourBookingResponse booking = bookingMapper.getBookingById(bookingId);
		
		if (booking == null) {
	        throw new IllegalArgumentException("예약 정보를 찾을 수 없습니다.");
	    }

	    if (booking.getMno() != mno) {
	        throw new AccessDeniedException("해당 예약에 대한 결제 권한이 없습니다.");
	    }

	    bookingMapper.updatePaymentStatus(bookingId, PaymentStatus.PAID, BookingStatus.RESERVED);
	}


}
