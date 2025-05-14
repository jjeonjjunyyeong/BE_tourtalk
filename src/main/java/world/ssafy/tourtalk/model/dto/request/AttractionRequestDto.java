package world.ssafy.tourtalk.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.Attraction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionRequestDto {
    // 기본 식별자
    private Integer no;
    
    // 기본 정보
    private String title;
    
    // 분류 정보
    private Integer contentTypeId;
    
    // 위치 정보
    private Integer sidoCode;
    private Integer gugunCode;
    private Double latitude;
    private Double longitude;
    
    // 상세 정보
    private String firstImage1;
    private String firstImage2;
    private String tel;
    private String addr;
    private String addr2;
    private String homepage;
    private String overview;
    
    // Attraction으로 변환하는 메서드 (호환성 유지)
    public Attraction toAttraction() {
        return Attraction.builder()
                .no(this.no != null ? this.no : 0)
                .title(this.title)
                .contentTypeId(this.contentTypeId)
                .sidoCode(this.sidoCode)
                .gugunCode(this.gugunCode)
                .latitude(this.latitude != null ? this.latitude : 0.0)
                .longitude(this.longitude != null ? this.longitude : 0.0)
                .firstImage1(this.firstImage1)
                .firstImage2(this.firstImage2)
                .tel(this.tel)
                .addr(this.addr)
                .addr2(this.addr2)
                .homepage(this.homepage)
                .overview(this.overview)
                .build();
    }
    
    // Attraction으로부터 생성하는 정적 팩토리 메서드
    public static AttractionRequestDto from(Attraction attraction) {
        return AttractionRequestDto.builder()
                .no(attraction.getNo())
                .title(attraction.getTitle())
                .contentTypeId(attraction.getContentTypeId())
                .sidoCode(attraction.getSidoCode())
                .gugunCode(attraction.getGugunCode())
                .latitude(attraction.getLatitude())
                .longitude(attraction.getLongitude())
                .firstImage1(attraction.getFirstImage1())
                .firstImage2(attraction.getFirstImage2())
                .tel(attraction.getTel())
                .addr(attraction.getAddr())
                .addr2(attraction.getAddr2())
                .homepage(attraction.getHomepage())
                .overview(attraction.getOverview())
                .build();
    }
}