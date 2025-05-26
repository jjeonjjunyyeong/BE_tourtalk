package world.ssafy.tourtalk.model.dto.response.route;

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
public class RouteResponseDto {
	private RouteInfo routeInfo;
	private List<RouteSection> sections;
	private List<Coordinate> coordinates;
	
	@Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteInfo{
		private Integer totalDistance;
		private Integer totalTime;
		private Integer tollFare;
		private Integer taxiFare;
		
		private Coordinate origin;
		private Coordinate destination;
		
		private List<Coordinate> waypoints;
		
		public double getDistanceInKm() {
			return totalDistance != null ? totalDistance / 1000.0 : 0.0;
		}
		
		public int getTimeInMinutes() {
            return totalTime != null ? totalTime / 60 : 0;
        }
		public String getFormattedTime() {
            if (totalTime == null) return "0분";
            
            int hours = totalTime / 3600;
            int minutes = (totalTime % 3600) / 60;
            
            if (hours > 0) {
                return String.format("%d시간 %d분", hours, minutes);
            } else {
                return String.format("%d분", minutes);
            }
        }

        public String getFormattedDistance() {
            if (totalDistance == null) return "0km";
            
            if (totalDistance < 1000) {
                return totalDistance + "m";
            } else {
                return String.format("%.1fkm", getDistanceInKm());
            }
        }
	}
	
	@Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteSection{
		private Integer distance;
		private Integer duration;
		private Integer trafficState; // 교통 상황 (0: 원활, 1: 서행, 2: 정체)
		
		private List<Coordinate> roads;
		
		private String description;
		
	}
}
