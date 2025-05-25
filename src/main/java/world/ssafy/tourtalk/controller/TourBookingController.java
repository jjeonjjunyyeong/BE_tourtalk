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

	@Operation(summary = "예약 등록", description = "회원이 상품을 보고 예약 신청")
	@PostMapping
	public ResponseEntity<?> reserve(@AuthenticationPrincipal CustomMemberPrincipal principal,
			@RequestBody TourBookingRequest request) {
		try {
			boolean success = bookingService.reserve(principal.getMno(), request);
			return success ? ResponseEntity.status(HttpStatus.CREATED).body("예약 성공!")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("예약 실패!");
		} catch (DataAccessException e) {
			log.error("예약 처리 중 오류", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
		}
	}

	@Operation(summary = "예약 인원 확인", description = "상품 별 예약된 인원 수 확인")
	@GetMapping("product/{productId}/counts")
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
}
