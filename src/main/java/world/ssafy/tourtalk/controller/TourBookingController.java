package world.ssafy.tourtalk.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.request.TourBookingRequest;
import world.ssafy.tourtalk.model.dto.response.TourBookingResponse;
import world.ssafy.tourtalk.model.service.TourBookingService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tourBooking")
public class TourBookingController {

	private final TourBookingService bookingService;

	@Operation(summary = "예약 등록", description = "사용자가 예약 정보를 제출하면 예약을 생성합니다.")
	@PostMapping
	public ResponseEntity<?> reserve(@AuthenticationPrincipal CustomMemberPrincipal principal,
			@RequestBody TourBookingRequest request) {
		try {			
			if (principal == null) {
		        log.warn("🔴 인증 정보 없음: principal == null");
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		    }
			
			
			boolean result  = bookingService.reserve(principal.getMno(), request);
			return result  ? ResponseEntity.status(HttpStatus.CREATED).body("예약 성공!")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("예약 실패!");
		} catch (DataAccessException e) {
			log.error("예약 처리 중 오류", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
		}
	}

	@Operation(summary = "예약 인원 확인", description = "상품 별 예약된 인원 수 확인")
	@GetMapping("/product/{productId}/counts")
	public ResponseEntity<?> getBookingCounts(@PathVariable int productId) {
		try {
			List<TourBookingResponse> list = bookingService.getBookingCounts(productId);
			
			Map<String, Integer> result = list.stream()
					.collect(Collectors.toMap(
						r -> r.getTime().toString(), 
						TourBookingResponse::getParticipantCount
					));
			
			return ResponseEntity.ok(result);
		} catch (DataAccessException e) {
			log.error("예약 처리 중 오류", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
		}
	}
	
	@Operation(summary = "예약 확인", description = "현재 로그인한 회원의 예약된 상품 목록 확인")
	@GetMapping("/member")
	public ResponseEntity<?> getMyBookings(@AuthenticationPrincipal CustomMemberPrincipal principal) {
		try {
	        List<TourBookingResponse> bookings = bookingService.getBookingsByMember(principal.getMno());
	        return bookings != null
	        		? ResponseEntity.ok(bookings)
	        		: ResponseEntity.status(HttpStatus.NOT_FOUND).body("예약한 상품이 없습니다.");
		} catch(DataAccessException e) {
			log.error("예약 처리 중 오류", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
		}
	}
	
	@Operation(summary = "예약 취소", description = "회원이 예약을 취소함")
	@PutMapping("/{bookingId}/cancel")
	public ResponseEntity<?> cancelBooking(@PathVariable int bookingId, @AuthenticationPrincipal CustomMemberPrincipal principal) {
	    try {
	        boolean result = bookingService.cancelBooking(bookingId, principal.getMno());
	        return result ? ResponseEntity.ok("예약이 취소되었습니다.") : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("취소 실패");
	    } catch (DataAccessException e) {
	        log.error("예약 취소 중 오류", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
	    }
	}

	@Operation(summary = "결제 완료 처리")
	@PutMapping("/{bookingId}/pay")
	public ResponseEntity<?> confirmPayment(@PathVariable int bookingId, @AuthenticationPrincipal CustomMemberPrincipal principal) {
	    try {
	    	bookingService.confirmPayment(bookingId, principal.getMno());
	        return ResponseEntity.ok("결제 상태 업데이트 완료");
	    } catch (Exception e) {
	        log.error("결제 상태 업데이트 오류", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 처리 실패");
	    }
	}
	
	
	 
}
