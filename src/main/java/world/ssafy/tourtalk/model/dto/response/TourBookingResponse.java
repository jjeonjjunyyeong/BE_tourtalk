package world.ssafy.tourtalk.model.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.BookingStatus;
import world.ssafy.tourtalk.model.dto.enums.PaymentStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourBookingResponse {
	private int bookingId;
	private int mno;
	private int productId;
	private LocalTime time;
	private LocalDateTime reservedAt;
	private int participantCount;
	private int totalPrice;
	private String paymentMethod;
	private PaymentStatus paymentStatus;
	private BookingStatus status;
	private LocalDateTime cancelledAt;
	
	public TourBookingResponse(LocalTime time, int participantCount) {
		this.time = time;
		this.participantCount = participantCount;
	}
}
