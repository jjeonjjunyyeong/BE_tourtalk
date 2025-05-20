package world.ssafy.tourtalk.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.SearchCondition;
import world.ssafy.tourtalk.model.dto.request.attraction.AttractionCreateUpdateRequestDto;
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
    public int getContentTypeIdByName(String contentTypeName) {
        List<Map<String, Object>> contents = attractionMapper.getContent();
        for (Map<String, Object> content : contents) {
            if (contentTypeName.equals(content.get("name"))) {
                return Integer.parseInt(content.get("id").toString());
            }
        }
        return 0;
    }

    @Override
    public int getSidoCodeByName(String sidoName) {
        List<Map<String, Object>> sidos = attractionMapper.getSido();
        for (Map<String, Object> sido : sidos) {
            if (sidoName.equals(sido.get("name"))) {
                return Integer.parseInt(sido.get("id").toString());
            }
        }
        return 0;
    }

    @Override
    public int getGugunCodeByName(String gugunName, int sidoCode) {
        List<Map<String, Object>> guguns = attractionMapper.getGugun(String.valueOf(sidoCode));
        for (Map<String, Object> gugun : guguns) {
            if (gugunName.equals(gugun.get("name"))) {
                return Integer.parseInt(gugun.get("id").toString());
            }
        }
        return 0;
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
        int offset = (pageNumber - 1) * pageSize;
        
        List<Attraction> attractions = attractionMapper.getAttractionByDirectCodesWithPaging(
                contentTypeId, sidoCode, gugunCode, offset, pageSize);
        
        int totalCount = attractionMapper.getTotalCountByDirectCodes(
                contentTypeId, sidoCode, gugunCode);
        
        return createPage(attractions, pageNumber, pageSize, totalCount);
    }
    
    @Override
    public Page<Attraction> searchAttractionsByCodes(SearchCondition condition) {
        if (condition == null) {
            condition = new SearchCondition();
        }
        
        condition.setDefaults();
        
        List<Attraction> attractions = attractionMapper.searchAttractionsByCodes(condition);
        int totalCount = attractionMapper.countAttractionsByCodes(condition);
        
        return createPage(attractions, condition.getPageNumber(), condition.getPageSize(), totalCount);
    }
    
    @Override
    @Transactional
    public Attraction createAttraction(AttractionCreateUpdateRequestDto requestDto) {
        if (!validateReferences(requestDto.getContentTypeId(), requestDto.getAreaCode(), requestDto.getSiGunGuCode())) {
            throw new IllegalArgumentException("존재하지 않는 컨텐츠 타입, 시도 또는 구군 코드입니다.");
        }
        
        if (existsAttractionByTitle(requestDto.getTitle())) {
            throw new IllegalArgumentException("이미 존재하는 관광지 이름입니다: " + requestDto.getTitle());
        }
        
        Attraction attraction = Attraction.builder()
                .contentId(requestDto.getContentId())
                .title(requestDto.getTitle())
                .contentTypeId(requestDto.getContentTypeId())
                .sidoCode(requestDto.getAreaCode())
                .gugunCode(requestDto.getSiGunGuCode())
                .latitude(requestDto.getLatitude() != null ? requestDto.getLatitude() : 0.0)
                .longitude(requestDto.getLongitude() != null ? requestDto.getLongitude() : 0.0)
                .firstImage1(requestDto.getFirstImage1())
                .firstImage2(requestDto.getFirstImage2())
                .mapLevel(requestDto.getMapLevel())
                .tel(requestDto.getTel())
                .addr(requestDto.getAddr1())
                .addr2(requestDto.getAddr2())
                .homepage(requestDto.getHomepage())
                .overview(requestDto.getOverview())
                .viewCnt(0)
                .build();
        
        int insertedCount = attractionMapper.insertAttraction(attraction);
        if (insertedCount == 0) {
            throw new RuntimeException("관광지 생성에 실패했습니다.");
        }
        
        return attractionMapper.getAttractionByNo(attraction.getNo());
    }
    
    @Override
    @Transactional
    public Attraction updateAttraction(int no, AttractionCreateUpdateRequestDto requestDto) {
        Attraction existingAttraction = attractionMapper.getAttractionByNo(no);
        if (existingAttraction == null) {
            throw new IllegalArgumentException("존재하지 않는 관광지입니다: " + no);
        }
        
        if (!validateReferences(requestDto.getContentTypeId(), requestDto.getAreaCode(), requestDto.getSiGunGuCode())) {
            throw new IllegalArgumentException("존재하지 않는 컨텐츠 타입, 시도 또는 구군 코드입니다.");
        }
        
        if (!existingAttraction.getTitle().equals(requestDto.getTitle()) && 
            existsAttractionByTitle(requestDto.getTitle())) {
            throw new IllegalArgumentException("이미 존재하는 관광지 이름입니다: " + requestDto.getTitle());
        }
        
        Attraction updatedAttraction = Attraction.builder()
                .no(no)
                .contentId(requestDto.getContentId())
                .title(requestDto.getTitle())
                .contentTypeId(requestDto.getContentTypeId())
                .sidoCode(requestDto.getAreaCode())
                .gugunCode(requestDto.getSiGunGuCode())
                .latitude(requestDto.getLatitude() != null ? requestDto.getLatitude() : existingAttraction.getLatitude())
                .longitude(requestDto.getLongitude() != null ? requestDto.getLongitude() : existingAttraction.getLongitude())
                .firstImage1(requestDto.getFirstImage1())
                .firstImage2(requestDto.getFirstImage2())
                .mapLevel(requestDto.getMapLevel())
                .tel(requestDto.getTel())
                .addr(requestDto.getAddr1())
                .addr2(requestDto.getAddr2())
                .homepage(requestDto.getHomepage())
                .overview(requestDto.getOverview())
                .viewCnt(existingAttraction.getViewCnt())
                .build();
        
        int updatedCount = attractionMapper.updateAttraction(updatedAttraction);
        if (updatedCount == 0) {
            throw new RuntimeException("관광지 수정에 실패했습니다.");
        }
        
        return attractionMapper.getAttractionByNo(no);
    }
    
    @Override
    @Transactional
    public boolean deleteAttraction(int no) {
        Attraction existingAttraction = attractionMapper.getAttractionByNo(no);
        if (existingAttraction == null) {
            throw new IllegalArgumentException("존재하지 않는 관광지입니다: " + no);
        }
        
        int deletedCount = attractionMapper.deleteAttraction(no);
        return deletedCount > 0;
    }
    
    @Override
    public boolean existsAttractionByTitle(String title) {
        return attractionMapper.countAttractionByTitle(title) > 0;
    }
    
    @Override
    public boolean validateReferences(int contentTypeId, int areaCode, int siGunGuCode) {
        if (attractionMapper.countContentTypeById(contentTypeId) == 0) {
            return false;
        }
        
        if (attractionMapper.countSidoByCode(areaCode) == 0) {
            return false;
        }
        
        if (attractionMapper.countGugunByCode(siGunGuCode) == 0) {
            return false;
        }
        
        return true;
    }
    
    private Page<Attraction> createPage(List<Attraction> content, int pageNumber, int pageSize, long totalCount) {
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        Page<Attraction> page = Page.<Attraction>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalCount)
                .totalPages(totalPages)
                .build();
        
        page.calculatePageInfo(10);
        
        return page;
    }
}