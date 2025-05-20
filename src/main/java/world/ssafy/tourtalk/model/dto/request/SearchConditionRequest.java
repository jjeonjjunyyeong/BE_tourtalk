package world.ssafy.tourtalk.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchConditionRequest {

	 // 페이징 관련
    private Integer pageNumber;
    private Integer pageSize;
    
    // 정렬 관련
    private String orderBy;
    private String orderDirection;
    
    // 검색 조건 관련 (관광지)
    private Integer contentTypeId;
    private Integer sidoCode;
    private Integer gugunCode;
    // 공통
    private String keyword;
    private String keywordType;
    // 게시글
    private int categoryId;
    private int writerId;
    
    
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
    public SearchConditionRequest toSearchCondition() {
    	SearchConditionRequest condition = new SearchConditionRequest();
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
    public static SearchConditionRequest from(SearchConditionRequest condition) {
        return SearchConditionRequest.builder()
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

    // 게시글 검색
	public SearchConditionRequest(Integer pageNumber, Integer pageSize, String orderBy, String orderDirection,
			String keyword, String keywordType, int categoryId, int writerId, Integer minViewCount, Boolean onlyWithImage) {
		super();
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.orderBy = orderBy;
		this.orderDirection = orderDirection;
		this.keyword = keyword;
		this.keywordType = keywordType;
		this.categoryId = categoryId;
		this.writerId = writerId;
		this.minViewCount = minViewCount;
		this.onlyWithImage = onlyWithImage;
	}
	
	// 검색 조건 존재 여부 확인
	public boolean hasSearchCondition() {
		return (keyword != null && !keyword.isBlank()) || writerId >  0 && categoryId > 0;
	}
}
