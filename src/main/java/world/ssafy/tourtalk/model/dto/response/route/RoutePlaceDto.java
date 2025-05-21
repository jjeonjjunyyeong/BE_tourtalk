package world.ssafy.tourtalk.model.dto.response.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.response.attraction.AttractionResponseDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RoutePlaceDto {
    private Integer placeId;
    private AttractionResponseDto attraction;
    private Integer visitOrder;
    private Integer estimatedTime;
    private String memo;
    private RouteTransportDto transport;
}
