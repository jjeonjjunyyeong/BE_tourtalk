package world.ssafy.tourtalk.model.dto.request.route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRecommendRequestDto {
    @NotEmpty(message = "관광지 ID 목록은 필수입니다")
    @Size(min = 2, max = 20, message = "관광지는 최소 2개, 최대 20개까지 선택 가능합니다")
    private List<Integer> attractionIds;
    
    @NotBlank(message = "최적화 기준은 필수입니다")
    @Pattern(regexp = "DISTANCE|TIME|RATING", message = "최적화 기준은 DISTANCE, TIME, RATING 중 하나여야 합니다")
    private String optimizeBy;
    
    @NotBlank(message = "이동 수단은 필수입니다")
    @Pattern(regexp = "WALK|CAR|BUS|SUBWAY|TRAIN|BICYCLE|TAXI", message = "이동 수단은 WALK, CAR, BUS, SUBWAY, TRAIN, BICYCLE, TAXI 중 하나여야 합니다")
    private String transportType;
    
    @NotNull(message = "시작 시간은 필수입니다")
    private LocalTime startTime;
    
    @NotNull(message = "종료 시간은 필수입니다")
    private LocalTime endTime;
    
    @Min(value = 1, message = "최대 일수는 최소 1일 이상이어야 합니다")
    @Max(value = 14, message = "최대 일수는 최대 14일까지 가능합니다")
    private Integer maxDays;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    private List<String> themes;
    private Boolean avoidHighways;
    private String title;
    private String description;
}



