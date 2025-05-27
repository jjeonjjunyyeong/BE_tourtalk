package world.ssafy.tourtalk.model.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.PriceType;
import world.ssafy.tourtalk.model.dto.enums.ProductStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourProductResponse {
	private int productId;
	private int mno;
	private int locationNo;
	private String title;
	private String description;
	private int maxParticipants;
	private int minParticipants;
	private PriceType priceType;
	private int price;
	private LocalDate startDate;
	private ProductStatus status;
	private String thumbnailImg;
	private String tags;
	private String meetingPlace;
	private int meetingTime;
	private int duration;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<LocalTime> timeSlots;
	
	public void setTimeSlots(List<LocalTime> timeSlots) {
		this.timeSlots = timeSlots;
	}
}