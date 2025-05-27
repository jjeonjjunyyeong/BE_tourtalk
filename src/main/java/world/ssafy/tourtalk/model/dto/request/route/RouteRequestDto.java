package world.ssafy.tourtalk.model.dto.request.route;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.response.common.Coordinate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDto {
	private Coordinate origin;
	private Coordinate destination;
	
	private List<Coordinate> waypoints;
	
	private String priority = "RECOMMEND";
	private String carFuel = "GASOLINE";
	
	private Boolean  carHipass = false;
	private Boolean alternatives = false;
	private Boolean roadDetails = true;
}
