package world.ssafy.tourtalk.model.dto.response.route;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RouteDayDto {
    private Integer dayId;
    private Integer dayNumber;
    private LocalDate date;
    private List<RoutePlaceDto> places;
    private Integer dayTotalTime;
}