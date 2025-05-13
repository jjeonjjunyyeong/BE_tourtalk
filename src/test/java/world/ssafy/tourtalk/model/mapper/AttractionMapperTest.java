package world.ssafy.tourtalk.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.SearchCondition;

@SpringBootTest
@Transactional  // 테스트 후 롤백하여 DB 상태를 원래대로 유지
public class AttractionMapperTest {

    @Autowired
    private AttractionMapper attractionMapper;

    @Test
    @DisplayName("컨텐츠 유형 목록 조회 테스트")
    void getContentTest() {
        // When
        List<Map<String, Object>> contentList = attractionMapper.getContent();

        // Then
        assertNotNull(contentList);
        assertTrue(contentList.size() > 0);
        System.out.println("Content List: " + contentList);
    }

    @Test
    @DisplayName("시도 목록 조회 테스트")
    void getSidoTest() {
        // When
        List<Map<String, Object>> sidoList = attractionMapper.getSido();

        // Then
        assertNotNull(sidoList);
        assertTrue(sidoList.size() > 0);
        System.out.println("Sido List: " + sidoList);
    }

    @Test
    @DisplayName("구군 목록 조회 테스트")
    void getGugunTest() {
        // Given - 첫 번째 시도 코드 가져오기
        List<Map<String, Object>> sidoList = attractionMapper.getSido();
        assertNotNull(sidoList);
        assertTrue(sidoList.size() > 0);
        
        String sidoCode = sidoList.get(0).get("id").toString();

        // When
        List<Map<String, Object>> gugunList = attractionMapper.getGugun(sidoCode);

        // Then
        assertNotNull(gugunList);
        System.out.println("Gugun List for sido " + sidoCode + ": " + gugunList);
    }

    @Test
    @DisplayName("관광지 번호로 조회 테스트")
    void getAttractionByNoTest() {
        // Given
        // DB에 존재하는 관광지 번호 찾기
        SearchCondition condition = new SearchCondition();
        condition.setPageNumber(1);
        condition.setPageSize(1);
        condition.setDefaults();
        
        List<Attraction> attractions = attractionMapper.searchAttractionsByCodes(condition);
        
        if (attractions.isEmpty()) {
            System.out.println("DB에 관광지 데이터가 없어 테스트를 진행할 수 없습니다.");
            return;
        }
        
        int no = attractions.get(0).getNo();

        // When
        Attraction attraction = attractionMapper.getAttractionByNo(no);

        // Then
        assertNotNull(attraction);
        assertEquals(no, attraction.getNo());
        System.out.println("검색된 관광지: " + attraction);
    }

    @Test
    @DisplayName("조회수 증가 테스트")
    @Transactional  // 이 테스트는 특히 롤백이 중요
    void updateViewCountTest() {
        // Given
        // DB에 존재하는 관광지 번호 찾기
        SearchCondition condition = new SearchCondition();
        condition.setPageNumber(1);
        condition.setPageSize(1);
        condition.setDefaults();
        
        List<Attraction> attractions = attractionMapper.searchAttractionsByCodes(condition);
        
        if (attractions.isEmpty()) {
            System.out.println("DB에 관광지 데이터가 없어 테스트를 진행할 수 없습니다.");
            return;
        }
        
        int no = attractions.get(0).getNo();
        
        Attraction before = attractionMapper.getAttractionByNo(no);
        assertNotNull(before);
        int beforeCount = before.getViewCnt();

        // When
        attractionMapper.updateViewCount(no);
        
        // Then
        Attraction after = attractionMapper.getAttractionByNo(no);
        assertNotNull(after);
        assertEquals(beforeCount + 1, after.getViewCnt());
        System.out.println("조회수 변경: " + beforeCount + " -> " + after.getViewCnt());
    }

    @Test
    @DisplayName("코드 기반 관광지 직접 조회 테스트")
    void getAttractionByDirectCodesTest() {
        // Given - DB에 존재하는 관광지 데이터 찾기
        SearchCondition condition = new SearchCondition();
        condition.setPageNumber(1);
        condition.setPageSize(1);
        condition.setDefaults();
        
        List<Attraction> attractions = attractionMapper.searchAttractionsByCodes(condition);
        
        if (attractions.isEmpty()) {
            System.out.println("DB에 관광지 데이터가 없어 테스트를 진행할 수 없습니다.");
            return;
        }
        
        Attraction sample = attractions.get(0);
        int contentTypeId = sample.getContentTypeId();
        int sidoCode = sample.getSidoCode();
        int gugunCode = sample.getGugunCode();

        // When
        List<Attraction> result = attractionMapper.getAttractionByDirectCodes(
                contentTypeId, sidoCode, gugunCode);

        // Then
        assertNotNull(result);
        System.out.println("검색된 관광지 수: " + result.size());
        if (!result.isEmpty()) {
            System.out.println("첫 번째 관광지: " + result.get(0));
        }
    }

    @Test
    @DisplayName("페이징 처리된 코드 기반 관광지 조회 테스트")
    void getAttractionByDirectCodesWithPagingTest() {
        // Given - DB에 존재하는 관광지 데이터 찾기
        SearchCondition condition = new SearchCondition();
        condition.setPageNumber(1);
        condition.setPageSize(1);
        condition.setDefaults();
        
        List<Attraction> attractions = attractionMapper.searchAttractionsByCodes(condition);
        
        if (attractions.isEmpty()) {
            System.out.println("DB에 관광지 데이터가 없어 테스트를 진행할 수 없습니다.");
            return;
        }
        
        Attraction sample = attractions.get(0);
        int contentTypeId = sample.getContentTypeId();
        int sidoCode = sample.getSidoCode();
        int gugunCode = sample.getGugunCode();
        
        int offset = 0;
        int limit = 5;

        // When
        List<Attraction> result = attractionMapper.getAttractionByDirectCodesWithPaging(
                contentTypeId, sidoCode, gugunCode, offset, limit);

        // Then
        assertNotNull(result);
        assertTrue(result.size() <= limit);
        System.out.println("페이징 처리된 관광지 수: " + result.size());
    }

    @Test
    @DisplayName("총 항목 수 조회 테스트")
    void getTotalCountByDirectCodesTest() {
        // Given - DB에 존재하는 관광지 데이터 찾기
        SearchCondition condition = new SearchCondition();
        condition.setPageNumber(1);
        condition.setPageSize(1);
        condition.setDefaults();
        
        List<Attraction> attractions = attractionMapper.searchAttractionsByCodes(condition);
        
        if (attractions.isEmpty()) {
            System.out.println("DB에 관광지 데이터가 없어 테스트를 진행할 수 없습니다.");
            return;
        }
        
        Attraction sample = attractions.get(0);
        int contentTypeId = sample.getContentTypeId();
        int sidoCode = sample.getSidoCode();
        int gugunCode = sample.getGugunCode();

        // When
        int totalCount = attractionMapper.getTotalCountByDirectCodes(
                contentTypeId, sidoCode, gugunCode);

        // Then
        assertTrue(totalCount >= 0);
        System.out.println("총 관광지 수: " + totalCount);
    }

    @Test
    @DisplayName("SearchCondition을 이용한 동적 검색 테스트")
    void searchAttractionsByCodesTest() {
        // Given
        SearchCondition condition = new SearchCondition();
        condition.setPageNumber(1);
        condition.setPageSize(10);
        condition.setDefaults();

        // When
        List<Attraction> attractions = attractionMapper.searchAttractionsByCodes(condition);

        // Then
        assertNotNull(attractions);
        System.out.println("검색된 관광지 수: " + attractions.size());
        if (!attractions.isEmpty()) {
            System.out.println("첫 번째 관광지: " + attractions.get(0));
        }
    }

    @Test
    @DisplayName("랜덤 관광지 조회 테스트")
    void getRandomAttractionsTest() {
        // Given
        int count = 5;

        // When
        List<Attraction> attractions = attractionMapper.getRandomAttractions(
                count, null, null);

        // Then
        assertNotNull(attractions);
        assertTrue(attractions.size() <= count);
        System.out.println("랜덤 관광지 수: " + attractions.size());
        if (!attractions.isEmpty()) {
            System.out.println("첫 번째 랜덤 관광지: " + attractions.get(0));
        }
    }
}