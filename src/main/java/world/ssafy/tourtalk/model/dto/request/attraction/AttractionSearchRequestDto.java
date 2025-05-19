package world.ssafy.tourtalk.model.dto.request.attraction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionSearchRequestDto {
    // 페이징 관련
    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다")
    private Integer pageNumber;
    
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
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
    
    // SearchCondition 객체로 변환하는 메서드
    public world.ssafy.tourtalk.model.dto.SearchCondition toSearchCondition() {
        world.ssafy.tourtalk.model.dto.SearchCondition condition = new world.ssafy.tourtalk.model.dto.SearchCondition();
        condition.setPageNumber(this.pageNumber);
        condition.setPageSize(this.pageSize);
        condition.setOrderBy(this.orderBy);
        condition.setOrderDirection(this.orderDirection);
        condition.setContentTypeId(this.contentTypeId);
        condition.setSidoCode(this.sidoCode);
        condition.setGugunCode(this.gugunCode);
        condition.setKeyword(this.keyword);
        condition.setMinViewCount(this.minViewCount);
        condition.setOnlyWithImage(this.onlyWithImage);
        return condition;
    }
}