package world.ssafy.tourtalk.model.dto.response.hotplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.Hotplace;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotplaceResponseDto {
    // 기본 식별자
    private Long id;
    private String userId;
    
    // 기본 정보
    private String title;
    private double latitude;
    private double longitude;
    private int rating;
    
    // 카테고리
    private int contentTypeId;
    private String contentTypeName;
    
    // 선택 정보
    private String description;
    private String review;
    private String recommendationReason;
    
    // 이미지
    private List<String> imageUrls;
    
    // 메타데이터
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isOwner;
    private int viewCount;
    
    // Hotplace 엔티티로부터 응답 DTO 생성
    public static HotplaceResponseDto from(Hotplace hotplace) {
        return HotplaceResponseDto.builder()
                .id(hotplace.getId())
                .userId(hotplace.getUserId())
                .title(hotplace.getTitle())
                .latitude(hotplace.getLatitude())
                .longitude(hotplace.getLongitude())
                .rating(hotplace.getRating())
                .contentTypeId(hotplace.getContentTypeId())
                .contentTypeName(hotplace.getContentTypeName())
                .description(hotplace.getDescription())
                .review(hotplace.getReview())
                .recommendationReason(hotplace.getRecommendationReason())
                .imageUrls(hotplace.getImageUrls())
                .createdAt(hotplace.getCreatedAt())
                .updatedAt(hotplace.getUpdatedAt())
                .isOwner(hotplace.isOwner())
                .viewCount(hotplace.getViewCount())
                .build();
    }
}