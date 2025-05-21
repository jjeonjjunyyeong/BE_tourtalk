package world.ssafy.tourtalk.model.dto.response.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RouteTransportDto {
    private String transportType;
    private Integer distance;
    private Integer estimatedTime;
    private String description;
}