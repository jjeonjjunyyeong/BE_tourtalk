package world.ssafy.tourtalk.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.request.ReservationRequest;
import world.ssafy.tourtalk.model.dto.response.ReservationResponse;
import world.ssafy.tourtalk.model.mapper.ReservationMapper;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationMapper reservationMapper;
	
	public boolean reserve(ReservationRequest request, int mno) {
		return reservationMapper.insert(request, mno) > 0;
	}

	public List<ReservationResponse> getReservationsByMno(int mno) {
		return reservationMapper.selectAllByMno(mno);
	}

	public boolean cancel(int mno, int reservationId) {
		return reservationMapper.cancelReservation(mno, reservationId) > 0;
	}
	
}