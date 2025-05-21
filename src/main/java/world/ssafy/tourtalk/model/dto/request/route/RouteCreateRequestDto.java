package world.ssafy.tourtalk.model.dto.request.route;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteCreateRequestDto {
    @NotBlank(message = "경로 제목은 필수입니다")
    @Size(max = 100, message = "경로 제목은 100자 이내여야 합니다")
    private String title;
    
    @Size(max = 1000, message = "경로 설명은 1000자 이내여야 합니다")
    private String description;
    
    private Boolean isPublic;
    private LocalDate startDate;
    private LocalDate endDate;
    
    @NotEmpty(message = "방문 장소 목록은 필수입니다")
    @Size(min = 1, message = "최소 1개 이상의 방문 장소가 필요합니다")
    private List<RoutePlaceRequestDto> places;
}