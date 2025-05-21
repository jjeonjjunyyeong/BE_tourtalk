package world.ssafy.tourtalk.model.dto.response.route;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponseDto {
    private Integer routeId;
    private String title;
    private String description;
    private Boolean isPublic;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer viewCount;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RouteDayDto> days;

    // 경로 요약 정보
    private Integer totalAttractions;
    private Integer totalDays;
    private String mainTheme;
}





