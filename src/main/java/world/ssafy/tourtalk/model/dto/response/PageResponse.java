package world.ssafy.tourtalk.model.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
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
    
    // Page로부터 변환하는 정적 팩토리 메서드 (단순 복사)
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getPageNumber())
                .pageSize(page.getPageSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .first(page.isFirst())
                .last(page.isLast())
                .startPage(page.getStartPage())
                .endPage(page.getEndPage())
                .build();
    }
    
    // Page로부터 변환하는 정적 팩토리 메서드 (타입 변환 포함)
    public static <S, T> PageResponse<T> from(Page<S> page, java.util.function.Function<S, T> mapper) {
        List<T> transformedContent = page.getContent()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
                
        return PageResponse.<T>builder()
                .content(transformedContent)
                .pageNumber(page.getPageNumber())
                .pageSize(page.getPageSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .first(page.isFirst())
                .last(page.isLast())
                .startPage(page.getStartPage())
                .endPage(page.getEndPage())
                .build();
    }
    
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