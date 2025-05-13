package world.ssafy.tourtalk.model.dto;

import lombok.AllArgsConstructor;
<<<<<<< HEAD
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attraction {
    // 기본 식별자
    private int no;
    private int contentId;
    
    // 기본 정보
    private String title;
    
    // 분류 정보
    private int contentTypeId;  // 추가: 코드 값으로 직접 조회 지원
    private String contentTypeName;
    
    // 위치 정보
    private int sidoCode;  // 추가: 코드 값으로 직접 조회 지원
    private String sido;
    private int gugunCode;  // 추가: 코드 값으로 직접 조회 지원
    private String gugun;
    private double latitude;
    private double longitude;
    private Integer mapLevel;
    
    // 상세 정보
    private String firstImage1;
    private String firstImage2;
    private String tel;
    private String addr;
    private String addr2;  // 추가: 상세 주소 정보
    private String homepage;
    private String overview;
    
    // 통계 정보
    private int viewCnt;
    
    // 검색 및 정렬용 메타데이터
    private double distance;  // 추가: 현재 위치로부터의 거리 계산용
    private boolean hasImage;  // 추가: 이미지 유무 판별용 (계산된 필드)
    
    // 이미지 유무 확인 메서드
    public boolean isHasImage() {
        return firstImage1 != null && !firstImage1.isEmpty();
    }
    
    // 필터링용 편의 메서드
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }
        
        keyword = keyword.toLowerCase();
        return (title != null && title.toLowerCase().contains(keyword)) ||
               (addr != null && addr.toLowerCase().contains(keyword)) ||
               (overview != null && overview.toLowerCase().contains(keyword));
    }
}
=======
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Attraction {
	private int no;
	private String contentId;
	private String title;
	private String contentTypeName;	// 관광지 
	private String sido;	// 서울
	private String gugun;	// 종로구
	private String firstImage1;
	private String firstImage2;
	private int mapLevel;
	private float latitude;
	private float longitude;
	private String tel;
	private String addr;
	private String homepage;
	private String overview;
	private int viewCnt;
	
	public Attraction(int no, String title, String sido, int viewCnt) {
		super();
		this.no = no;
		this.title = title;
		this.sido = sido;
		this.viewCnt = viewCnt;
	}
}
>>>>>>> 87cc8738304e20ca99ab999cb5d0649a0e842364
