package world.ssafy.tourtalk.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.TripPlan;
import world.ssafy.tourtalk.model.dto.TripPlanAttraction;
import world.ssafy.tourtalk.model.dto.enums.TripPlanStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripPlanResponseDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalDistance;
    private Integer totalDuration;
    private TripPlanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TripPlanAttractionResponseDto> attractions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripPlanAttractionResponseDto {
        private Long id;
        private Integer attractionId;
        private Integer visitOrder;
        private String attractionTitle;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String sido;
        private String gugun;
        private String addr;
        
        public static TripPlanAttractionResponseDto from(TripPlanAttraction attraction) {
            return TripPlanAttractionResponseDto.builder()
                    .id(attraction.getId())
                    .attractionId(attraction.getAttractionNo())
                    .visitOrder(attraction.getVisitOrder())
                    .attractionTitle(attraction.getAttractionTitle())
                    .latitude(attraction.getLatitude())
                    .longitude(attraction.getLongitude())
                    .sido(attraction.getSido())
                    .gugun(attraction.getGugun())
                    .addr(attraction.getAddr())
                    .build();
        }
    }
    
    public static TripPlanResponseDto from(TripPlan tripPlan) {
        List<TripPlanAttractionResponseDto> attractionDtos = tripPlan.getAttractions() != null 
            ? tripPlan.getAttractions().stream()
                .map(TripPlanAttractionResponseDto::from)
                .collect(Collectors.toList())
            : List.of();
            
        return TripPlanResponseDto.builder()
                .id(tripPlan.getId())
                .name(tripPlan.getName())
                .description(tripPlan.getDescription())
                .startDate(tripPlan.getStartDate())
                .endDate(tripPlan.getEndDate())
                .totalDistance(tripPlan.getTotalDistance())
                .totalDuration(tripPlan.getTotalDuration())
                .status(tripPlan.getStatus())
                .createdAt(tripPlan.getCreatedAt())
                .updatedAt(tripPlan.getUpdatedAt())
                .attractions(attractionDtos)
                .build();
    }
}
