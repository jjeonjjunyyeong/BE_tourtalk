package world.ssafy.tourtalk.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutePlace {
   private Integer placeId;
   private Integer routeId;
   private Integer attractionNo;
   private Integer visitOrder;
   private Integer estimatedTime;
   private LocalDate visitDate;
   private String memo;
}
