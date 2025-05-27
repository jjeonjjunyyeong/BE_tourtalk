package world.ssafy.tourtalk.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.BoardCategory;
import world.ssafy.tourtalk.model.dto.enums.BoardStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardSearchRequest {

    // 페이징
    private Integer pageNumber;
    private Integer pageSize;

    // 정렬
    private String orderBy;
    private String orderDirection;

    // 검색 조건
    private String keyword;       // 제목 또는 작성자
    private String keywordType;   // title, writer 중 하나
    private BoardCategory category;
    private BoardStatus status;

    public void setDefaults() {
        if (pageNumber == null || pageNumber < 1) pageNumber = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (orderDirection == null || orderDirection.isBlank()) orderDirection = "DESC";
    }

    public int getOffset() {
        return (pageNumber - 1) * pageSize;
    }

    public boolean hasSearchCondition() {
        return (keyword != null && !keyword.isBlank()) || category != null || status != null;
    }
}
