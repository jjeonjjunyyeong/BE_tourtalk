package world.ssafy.tourtalk.model.dto.request;

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
public class TourBookingRequest {
	private int mno;
	private int productId;
	private LocalTime time;
	private int participantCount;
	private int totalPrice;
	private String paymentMethod;
	private PaymentStatus paymentStatus;
	private BookingStatus status;
}
