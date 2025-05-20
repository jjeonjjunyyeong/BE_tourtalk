package world.ssafy.tourtalk.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.SearchCondition;
import world.ssafy.tourtalk.model.mapper.AttractionMapper;

@ExtendWith(MockitoExtension.class)
public class BasicAttractionServiceTest {

    @Mock
    private AttractionMapper attractionMapper;

    @InjectMocks
    private BasicAttractionService attractionService;

    private Attraction testAttraction;
    private List<Attraction> attractionList;
    private List<Map<String, Object>> contentList;
    private List<Map<String, Object>> sidoList;
    private List<Map<String, Object>> gugunList;

    @BeforeEach
    void setUp() {
        // 테스트 관광지 생성
        testAttraction = Attraction.builder()
                .no(1)
                .contentId(1234)
                .title("테스트 관광지")
                .contentTypeId(12)
                .contentTypeName("관광지")
                .sidoCode(1)
                .sido("서울")
                .gugunCode(1)
                .gugun("종로구")
                .latitude(37.5665)
                .longitude(126.9780)
                .firstImage1("http://example.com/image1.jpg")
                .addr("서울시 종로구 테스트로 123")
                .overview("테스트 관광지 설명입니다.")
                .viewCnt(100)
                .build();

        // 관광지 목록 생성
        attractionList = new ArrayList<>();
        attractionList.add(testAttraction);
        attractionList.add(Attraction.builder()
                .no(2)
                .title("두 번째 관광지")
                .contentTypeId(12)
                .contentTypeName("관광지")
                .sidoCode(1)
                .sido("서울")
                .gugunCode(2)
                .gugun("강남구")
                .build());

        // 컨텐츠 타입 목록 생성
        contentList = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        content.put("id", 12);
        content.put("name", "관광지");
        contentList.add(content);

        // 시도 목록 생성
        sidoList = new ArrayList<>();
        Map<String, Object> sido = new HashMap<>();
        sido.put("id", 1);
        sido.put("name", "서울");
        sidoList.add(sido);

        // 구군 목록 생성
        gugunList = new ArrayList<>();
        Map<String, Object> gugun = new HashMap<>();
        gugun.put("id", 1);
        gugun.put("name", "종로구");
        gugunList.add(gugun);
        Map<String, Object> gugun2 = new HashMap<>();
        gugun2.put("id", 2);
        gugun2.put("name", "강남구");
        gugunList.add(gugun2);
    }

    @Test
    @DisplayName("ID로 관광지 조회 테스트")
    void getAttractionByNoTest() {
        // Given
        when(attractionMapper.getAttractionByNo(1)).thenReturn(testAttraction);

        // When
        Attraction result = attractionService.getAttractionByNo(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getNo());
        assertEquals("테스트 관광지", result.getTitle());
    }

    @Test
    @DisplayName("랜덤 관광지 조회 테스트")
    void getRandomAttractionsTest() {
        // Given
        when(attractionMapper.getRandomAttractions(eq(5), eq(null), eq(null)))
                .thenReturn(attractionList);

        // When
        List<Attraction> result = attractionService.getRandomAttractions(5);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("테마별 랜덤 관광지 조회 테스트")
    void getRandomAttractionsByThemeTest() {
        // Given
        when(attractionMapper.getRandomAttractions(eq(5), eq(12), eq(null)))
                .thenReturn(attractionList);

        // When
        List<Attraction> result = attractionService.getRandomAttractionsByTheme(5, 12);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(12, result.get(0).getContentTypeId());
    }

    @Test
    @DisplayName("지역별 랜덤 관광지 조회 테스트")
    void getRandomAttractionsByRegionTest() {
        // Given
        when(attractionMapper.getRandomAttractions(eq(5), eq(null), eq(1)))
                .thenReturn(attractionList);

        // When
        List<Attraction> result = attractionService.getRandomAttractionsByRegion(5, 1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getSidoCode());
    }

    @Test
    @DisplayName("코드 값을 직접 사용한 페이징 조회 테스트")
    void getAttractionsByDirectCodesWithPagingTest() {
        // Given
        when(attractionMapper.getAttractionByDirectCodesWithPaging(
                eq(12), eq(1), eq(1), anyInt(), eq(10)))
                .thenReturn(attractionList);
        when(attractionMapper.getTotalCountByDirectCodes(
                eq(12), eq(1), eq(1)))
                .thenReturn(2);

        // When
        Page<Attraction> result = attractionService.getAttractionsByDirectCodesWithPaging(
                12, 1, 1, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
    }

    @Test
    @DisplayName("동적 검색 조건을 이용한 조회 테스트")
    void searchAttractionsByCodesTest() {
        // Given
        SearchCondition condition = SearchCondition.builder()
                .contentTypeId(12)
                .sidoCode(1)
                .keyword("테스트")
                .pageNumber(1)
                .pageSize(10)
                .build();

        when(attractionMapper.searchAttractionsByCodes(any(SearchCondition.class)))
                .thenReturn(attractionList);
        when(attractionMapper.countAttractionsByCodes(any(SearchCondition.class)))
                .thenReturn(2);

        // When
        Page<Attraction> result = attractionService.searchAttractionsByCodes(condition);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
    }

    @Test
    @DisplayName("컨텐츠 타입 이름으로 ID 조회 테스트")
    void getContentTypeIdByNameTest() {
        // Given
        when(attractionMapper.getContent()).thenReturn(contentList);

        // When
        int result = attractionService.getContentTypeIdByName("관광지");

        // Then
        assertEquals(12, result);
    }

    @Test
    @DisplayName("시도 이름으로 코드 조회 테스트")
    void getSidoCodeByNameTest() {
        // Given
        when(attractionMapper.getSido()).thenReturn(sidoList);

        // When
        int result = attractionService.getSidoCodeByName("서울");

        // Then
        assertEquals(1, result);
    }

    @Test
    @DisplayName("구군 이름으로 코드 조회 테스트")
    void getGugunCodeByNameTest() {
        // Given
        when(attractionMapper.getGugun(anyString())).thenReturn(gugunList);

        // When
        int result = attractionService.getGugunCodeByName("종로구", 1);

        // Then
        assertEquals(1, result);
    }
}