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
public class RouteDay {
   private Integer dayId;
   private Integer routeId;
   private Integer dayNumber;
   private LocalDate date;
   private Integer dayTotalTime;
}