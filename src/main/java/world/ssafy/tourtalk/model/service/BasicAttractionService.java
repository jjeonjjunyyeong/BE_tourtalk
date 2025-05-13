package world.ssafy.tourtalk.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.SearchCondition;
import world.ssafy.tourtalk.model.mapper.AttractionMapper;

@Service
@RequiredArgsConstructor
public class BasicAttractionService implements AttractionService {

    private final AttractionMapper attractionMapper;
    
    @Override
    public List<Map<String, Object>> getContent() {
        return attractionMapper.getContent();
    }
    
    @Override
    public List<Map<String, Object>> getSido() {
        return attractionMapper.getSido();
    }
    
    @Override
    public List<Map<String, Object>> getGugun(String code) {
        return attractionMapper.getGugun(code);
    }
    
    @Override
    public Attraction getAttractionByNo(int no) {
        return attractionMapper.getAttractionByNo(no);
    }
    
    @Override
    public List<Attraction> getRandomAttractions(int count) {
        return attractionMapper.getRandomAttractions(count, null, null);
    }
    
    @Override
    public List<Attraction> getRandomAttractionsByTheme(int count, int contentTypeId) {
        return attractionMapper.getRandomAttractions(count, contentTypeId, null);
    }
    
    @Override
    public List<Attraction> getRandomAttractionsByRegion(int count, int sidoCode) {
        return attractionMapper.getRandomAttractions(count, null, sidoCode);
    }
    
    @Override
    public void updateViewCount(int no) {
        attractionMapper.updateViewCount(no);
    }
    
    @Override
    public List<Map<String, Object>> allCountView() {
        return attractionMapper.allCountView();
    }
    
    @Override
    public List<Attraction> getAttractionsByDirectCodes(int contentTypeId, int sidoCode, int gugunCode) {
        return attractionMapper.getAttractionByDirectCodes(contentTypeId, sidoCode, gugunCode);
    }
    
    @Override
    public Page<Attraction> getAttractionsByDirectCodesWithPaging(int contentTypeId, int sidoCode, int gugunCode, int pageNumber, int pageSize) {
        // 페이지 번호를 offset으로 변환
        int offset = (pageNumber - 1) * pageSize;
        
        // 조건에 맞는 관광지 목록 조회
        List<Attraction> attractions = attractionMapper.getAttractionByDirectCodesWithPaging(
                contentTypeId, sidoCode, gugunCode, offset, pageSize);
        
        // 조건에 맞는 총 관광지 수 조회
        int totalCount = attractionMapper.getTotalCountByDirectCodes(
                contentTypeId, sidoCode, gugunCode);
        
        return createPage(attractions, pageNumber, pageSize, totalCount);
    }
    
    @Override
    public Page<Attraction> searchAttractionsByCodes(SearchCondition condition) {
        if (condition == null) {
            condition = new SearchCondition();
        }
        
        // 기본값 설정
        condition.setDefaults();
        
        // 조건에 맞는 관광지 목록 조회
        List<Attraction> attractions = attractionMapper.searchAttractionsByCodes(condition);
        
        // 조건에 맞는 총 관광지 수 조회
        int totalCount = attractionMapper.countAttractionsByCodes(condition);
        
        return createPage(attractions, condition.getPageNumber(), condition.getPageSize(), totalCount);
    }
    
    // 페이지 생성 헬퍼 메서드
    private Page<Attraction> createPage(List<Attraction> content, int pageNumber, int pageSize, long totalCount) {
        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        // Page 객체 생성 및 반환
        Page<Attraction> page = Page.<Attraction>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalCount)
                .totalPages(totalPages)
                .build();
        
        // 내비게이션 정보 계산
        page.calculatePageInfo(10);
        
        return page;
    }
}