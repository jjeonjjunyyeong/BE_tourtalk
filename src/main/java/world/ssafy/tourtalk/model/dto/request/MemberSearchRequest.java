package world.ssafy.tourtalk.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.enums.MemberStatus;
import world.ssafy.tourtalk.model.dto.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSearchRequest {
	// 페이징
    private Integer pageNumber;
    private Integer pageSize;

    // 정렬
    private String orderBy;
    private String orderDirection;
    
 // 검색 조건
    private String keyword;        // id 또는 nickname
    private String keywordType;    // "id", "nickname" 중 선택
    private Role role;       // USER, CURATOR, ADMIN
    private MemberStatus status;   // ACTIVE, SUSPENDED, PENDING, DELETED

    // 기본값 설정 메서드
    public void setDefaults() {
        if (pageNumber == null || pageNumber < 1) pageNumber = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (orderDirection == null || orderDirection.isBlank()) orderDirection = "DESC";
    }

    // offset 계산
    public int getOffset() {
        return (pageNumber - 1) * pageSize;
    }

    // 검색 조건 존재 여부 확인
    public boolean hasSearchCondition() {
        return (keyword != null && !keyword.isBlank()) || role != null || status != null;
    }
}
