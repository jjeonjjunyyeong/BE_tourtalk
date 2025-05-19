package world.ssafy.tourtalk.model.dto.response.attraction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.Attraction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionResponseDto {
    // 기본 식별자
    private int no;
    private int contentId;
    
    // 기본 정보
    private String title;
    
    // 분류 정보
    private int contentTypeId;
    private String contentTypeName;
    
    // 위치 정보
    private int sidoCode;
    private String sido;
    private int gugunCode;
    private String gugun;
    private double latitude;
    private double longitude;
    private Integer mapLevel;
    
    // 상세 정보
    private String firstImage1;
    private String firstImage2;
    private String tel;
    private String addr;
    private String addr2;
    private String homepage;
    private String overview;
    
    // 통계 정보
    private int viewCnt;
    
    // Attraction 엔티티로부터 응답 DTO 생성
    public static AttractionResponseDto from(Attraction attraction) {
        return AttractionResponseDto.builder()
                .no(attraction.getNo())
                .contentId(attraction.getContentId())
                .title(attraction.getTitle())
                .contentTypeId(attraction.getContentTypeId())
                .contentTypeName(attraction.getContentTypeName())
                .sidoCode(attraction.getSidoCode())
                .sido(attraction.getSido())
                .gugunCode(attraction.getGugunCode())
                .gugun(attraction.getGugun())
                .latitude(attraction.getLatitude())
                .longitude(attraction.getLongitude())
                .mapLevel(attraction.getMapLevel())
                .firstImage1(attraction.getFirstImage1())
                .firstImage2(attraction.getFirstImage2())
                .tel(attraction.getTel())
                .addr(attraction.getAddr())
                .addr2(attraction.getAddr2())
                .homepage(attraction.getHomepage())
                .overview(attraction.getOverview())
                .viewCnt(attraction.getViewCnt())
                .build();
    }
}