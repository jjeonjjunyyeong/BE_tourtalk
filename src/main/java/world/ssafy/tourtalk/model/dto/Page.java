package world.ssafy.tourtalk.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {
    private List<T> content;          // 페이지에 포함된 항목 목록
    private int pageNumber;           // 현재 페이지 번호
    private int pageSize;             // 페이지당 항목 수
    private int totalPages;           // 총 페이지 수
    private long totalElements;       // 총 항목 수
    private boolean first;            // 첫 페이지 여부
    private boolean last;             // 마지막 페이지 여부
    
    // 내비게이션 관련 필드
    private int startPage;            // 내비게이션 시작 페이지
    private int endPage;              // 내비게이션 끝 페이지
    
    // 내비게이션 정보 계산 메서드
    public void calculatePageInfo(int navSize) {
        this.first = (pageNumber == 1);
        this.last = (pageNumber == totalPages);
        
        // 시작 페이지와 끝 페이지 계산
        int tempStartPage = ((pageNumber - 1) / navSize) * navSize + 1;
        int tempEndPage = tempStartPage + navSize - 1;
        
        // 끝 페이지가 총 페이지 수를 초과하지 않도록 조정
        if (tempEndPage > totalPages) {
            tempEndPage = totalPages;
        }
        
        this.startPage = tempStartPage;
        this.endPage = tempEndPage;
    }
}