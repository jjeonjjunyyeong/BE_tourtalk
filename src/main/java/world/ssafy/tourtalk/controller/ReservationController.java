package world.ssafy.tourtalk.controller;

import java.util.List;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.request.ReservationRequest;
import world.ssafy.tourtalk.model.dto.response.ReservationResponse;
import world.ssafy.tourtalk.model.service.ReservationService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {

	private final ReservationService reservationService;
	
	@PostMapping
	public ResponseEntity<?> reserve(@AuthenticationPrincipal CustomMemberPrincipal principal, @RequestBody ReservationRequest request) {
		try {
			boolean result = reservationService.reserve(request, principal.getMno());
			
			return result 
					? ResponseEntity.status(HttpStatus.CREATED).body("상품 예약 성공!")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 예약 실패!");
							
		} catch (DataAccessException e) {
			log.error("상품 예약 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	@GetMapping
	public ResponseEntity<?> getMyReservations(@AuthenticationPrincipal CustomMemberPrincipal principal) {
		try {
			List<ReservationResponse> list = reservationService.getReservationsByMno(principal.getMno());
			
			return list != null
					? ResponseEntity.ok(list)
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("예약 조회 실패!");
		} catch(DataAccessException e) {
			log.error("예약 조회 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
	
	@PutMapping("/{reservationId}/cancel")
	public ResponseEntity<?> cancelReservation(@PathVariable int reservationId,
            @AuthenticationPrincipal CustomMemberPrincipal principal) {
		try {
			boolean result = reservationService.cancel(principal.getMno(), reservationId);
			
			return result
					? ResponseEntity.status(HttpStatus.OK).body("예약이 취소되었습니다.")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 예약 취소 실패!");
		} catch(DataAccessException e) {
			log.error("예약 취소 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}
}