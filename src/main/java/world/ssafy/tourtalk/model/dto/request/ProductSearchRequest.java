package world.ssafy.tourtalk.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.ProductStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchRequest {

	// 페이징
    private Integer pageNumber;
    private Integer pageSize;

    // 정렬
    private String orderBy;
    private String orderDirection;

    // 검색 조건
    private String keyword;
    private String date; // 예약 가능일자 (yyyy-MM-dd)
    private Integer participantCount; // 예약 가능 인원

    // 상태 필터링 (예: OPEN 상태 상품만)
    private ProductStatus status;

    public void setDefaults() {
        if (pageNumber == null || pageNumber < 1) pageNumber = 1;
        if (pageSize == null || pageSize < 1) pageSize = 12;
        if (orderDirection == null) orderDirection = "DESC";
        if (status == null) status = ProductStatus.OPEN;
    }

    // offset 계산 메서드
    public int getOffset() {
        return (pageNumber - 1) * pageSize;
    }
	
}
