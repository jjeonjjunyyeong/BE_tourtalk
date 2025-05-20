package world.ssafy.tourtalk.model.dto.request.hotplace;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotplaceCreateRequestDto {
    
    // 필수 정보
    @NotBlank(message = "장소명은 필수입니다")
    @Size(max = 200, message = "장소명은 200자 이내여야 합니다")
    private String title;
    
    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "33.0", message = "위도는 33.0 이상이어야 합니다")
    @DecimalMax(value = "39.0", message = "위도는 39.0 이하여야 합니다")
    private Double latitude;
    
    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "124.0", message = "경도는 124.0 이상이어야 합니다")
    @DecimalMax(value = "132.0", message = "경도는 132.0 이하여야 합니다")
    private Double longitude;
    
    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5 이하여야 합니다")
    private Integer rating;
    
    @NotNull(message = "카테고리는 필수입니다")
    private Integer contentTypeId;
    
    // 선택 정보
    @Size(max = 1000, message = "설명은 1000자 이내여야 합니다")
    private String description;
    
    @Size(max = 2000, message = "리뷰는 2000자 이내여야 합니다")
    private String review;
    
    @Size(max = 500, message = "추천 이유는 500자 이내여야 합니다")
    private String recommendationReason;
    
    // 이미지 파일들 (최대 5개)
    @Size(max = 5, message = "이미지는 최대 5개까지 업로드 가능합니다")
    private List<MultipartFile> images;
}