package world.ssafy.tourtalk.model.dto.request.attraction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관광지 생성/수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionCreateUpdateRequestDto {
    
    // 필수 항목
    @NotBlank(message = "관광지 이름은 필수입니다")
    private String title;
    
    @NotNull(message = "컨텐츠 타입 ID는 필수입니다")
    private Integer contentTypeId;
    
    @NotNull(message = "시도 코드는 필수입니다")
    private Integer areaCode;
    
    @NotNull(message = "시군구 코드는 필수입니다")
    private Integer siGunGuCode;
    
    // 선택 항목
    private Integer contentId;
    
    @DecimalMin(value = "33.0", message = "위도는 33.0 이상이어야 합니다")
    @DecimalMax(value = "39.0", message = "위도는 39.0 이하여야 합니다")
    private Double latitude;
    
    @DecimalMin(value = "124.0", message = "경도는 124.0 이상이어야 합니다")
    @DecimalMax(value = "132.0", message = "경도는 132.0 이하여야 합니다")
    private Double longitude;
    
    private String firstImage1;
    private String firstImage2;
    private Integer mapLevel;
    private String tel;
    private String addr1;
    private String addr2;
    private String homepage;
    private String overview;
}