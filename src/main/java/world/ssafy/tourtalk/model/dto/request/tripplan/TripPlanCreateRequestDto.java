package world.ssafy.tourtalk.model.dto.request.tripplan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.TripPlanStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanCreateRequestDto {
    
    @NotBlank(message = "여행 계획명은 필수입니다")
    @Size(max = 200, message = "여행 계획명은 200자 이내여야 합니다")
    private String name;
    
    @Size(max = 2000, message = "설명은 2000자 이내여야 합니다")
    private String description;
    
    @NotNull(message = "시작 날짜는 필수입니다")
    private LocalDate startDate;
    
    @NotNull(message = "종료 날짜는 필수입니다")
    private LocalDate endDate;
    
    private BigDecimal totalDistance;
    
    private Integer totalDuration;
    
    @Builder.Default
    private TripPlanStatus status = TripPlanStatus.DRAFT;
    
    @NotEmpty(message = "관광지 목록은 비어있을 수 없습니다")
    @Valid
    private List<TripPlanAttractionRequestDto> attractions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripPlanAttractionRequestDto {
        
        @NotNull(message = "관광지 번호는 필수입니다")
        private Integer attractionId;
        
        @NotNull(message = "방문 순서는 필수입니다")
        private Integer visitOrder;
        
        @NotBlank(message = "관광지 제목은 필수입니다")
        private String attractionTitle;
        
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String sido;
        private String gugun;
        private String addr;
    }
}