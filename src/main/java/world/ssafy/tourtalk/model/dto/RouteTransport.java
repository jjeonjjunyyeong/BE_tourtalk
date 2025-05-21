package world.ssafy.tourtalk.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteTransport {
   private Integer transportId;
   private Integer routeId;
   private Integer fromPlaceId;
   private Integer toPlaceId;
   private String transportType;
   private Integer distance;
   private Integer estimatedTime;
   private String description;
}
