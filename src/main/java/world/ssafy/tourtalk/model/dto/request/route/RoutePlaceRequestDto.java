package world.ssafy.tourtalk.model.dto.request.route;
import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutePlaceRequestDto {
    @NotNull(message = "관광지 번호는 필수입니다")
    private Integer attractionNo;
    
    @NotNull(message = "방문 순서는 필수입니다")
    @Min(value = 1, message = "방문 순서는 1 이상이어야 합니다")
    private Integer visitOrder;
    
    private LocalDate visitDate;
    
    @Min(value = 10, message = "예상 소요 시간은 최소 10분 이상이어야 합니다")
    @Max(value = 480, message = "예상 소요 시간은 최대 480분(8시간)까지 가능합니다")
    private Integer estimatedTime;
    
    private String memo;
    private String transportType;
}