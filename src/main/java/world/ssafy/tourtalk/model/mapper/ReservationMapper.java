package world.ssafy.tourtalk.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.request.ReservationRequest;
import world.ssafy.tourtalk.model.dto.response.ReservationResponse;

@Mapper
public interface ReservationMapper {

	int insert(@Param("request")ReservationRequest request, @Param("mno")int mno);

	List<ReservationResponse> selectAllByMno(@Param("mno")int mno);

	int cancelReservation(@Param("mno")int mno, @Param("reservationId")int reservationId);

}