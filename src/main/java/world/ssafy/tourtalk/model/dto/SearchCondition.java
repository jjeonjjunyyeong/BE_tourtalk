package world.ssafy.tourtalk.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCondition {
    // 페이징 관련
    private Integer pageNumber;
    private Integer pageSize;
    
    // 정렬 관련
    private String orderBy;
    private String orderDirection;
    
    // 검색 조건 관련
    private Integer contentTypeId;
    private Integer sidoCode;
    private Integer gugunCode;
    private String keyword;
    
    // 추가 필터링
    private Integer minViewCount;
    private Boolean onlyWithImage;
    
    // 기본값 설정 메서드
    public void setDefaults() {
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        if (orderDirection == null) {
            orderDirection = "DESC";
        }
        
        if (minViewCount == null) {
            minViewCount = 0;
        }
        
        if (onlyWithImage == null) {
            onlyWithImage = false;
        }
    }
    
    // offset 계산 메서드
    public int getOffset() {
        return (pageNumber - 1) * pageSize;
    }
}