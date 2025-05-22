package world.ssafy.tourtalk.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanAttraction {
    private Long id;
    private Long tripPlanId;
    private Integer attractionNo;
    private Integer visitOrder;
    private String attractionTitle;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String sido;
    private String gugun;
    private String addr;
    private LocalDateTime createdAt;
}
