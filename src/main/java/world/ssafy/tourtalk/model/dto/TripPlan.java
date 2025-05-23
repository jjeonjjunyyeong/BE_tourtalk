package world.ssafy.tourtalk.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.TripPlanStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripPlan {
    private Long id;
    private Integer mno;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalDistance;
    private Integer totalDuration;
    private TripPlanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 연관된 관광지 목록
    private List<TripPlanAttraction> attractions;
}