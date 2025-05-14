package world.ssafy.tourtalk.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.SearchCondition;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchConditionRequestDto {
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
    
    // SearchCondition으로 변환하는 메서드 (호환성 유지)
    public SearchCondition toSearchCondition() {
        SearchCondition condition = new SearchCondition();
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
    
    // SearchCondition으로부터 생성하는 정적 팩토리 메서드
    public static SearchConditionRequestDto from(SearchCondition condition) {
        return SearchConditionRequestDto.builder()
                .pageNumber(condition.getPageNumber())
                .pageSize(condition.getPageSize())
                .orderBy(condition.getOrderBy())
                .orderDirection(condition.getOrderDirection())
                .contentTypeId(condition.getContentTypeId())
                .sidoCode(condition.getSidoCode())
                .gugunCode(condition.getGugunCode())
                .keyword(condition.getKeyword())
                .minViewCount(condition.getMinViewCount())
                .onlyWithImage(condition.getOnlyWithImage())
                .build();
    }
}