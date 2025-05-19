package world.ssafy.tourtalk.model.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.PaymentStatus;
import world.ssafy.tourtalk.model.dto.enums.ReservationStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {
	private int reservationId;
    private int productId;
    private int participantCount;
    private int totalPrice;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private ReservationStatus reservationStatus;
    private LocalDateTime reservedAt;
    private LocalDateTime cancelledAt;
}