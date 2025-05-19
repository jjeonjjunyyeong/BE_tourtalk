package world.ssafy.tourtalk.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hotplace {
    // 기본 식별자
    private Long id;
    private String userId;  // 임시 사용자 ID
    
    // 기본 정보 (NOT NULL)
    private String title;           // 장소명
    private double latitude;        // 위도
    private double longitude;       // 경도
    private int rating;            // 평점 (1-5)
    
    // 카테고리 (기존 contenttypes 활용)
    private int contentTypeId;
    private String contentTypeName; // JOIN으로 가져올 컬럼
    
    // 선택 정보 (NULL 가능)
    private String description;           // 설명
    private String review;               // 방문 후기
    private String recommendationReason;  // 추천 이유
    
    // 이미지 정보
    private List<String> imageUrls;  // 업로드된 이미지 URL들
    
    // 메타데이터
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 추가 정보 (계산된 값들)
    private boolean isOwner;  // 현재 사용자가 등록자인지 여부
    private int viewCount;    // 조회수
}