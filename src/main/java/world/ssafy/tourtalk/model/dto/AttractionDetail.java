package world.ssafy.tourtalk.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class AttractionDetail {
	private final Attraction attraction;
	private final Attraction[] nearbyAttractions;
}
