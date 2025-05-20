package world.ssafy.tourtalk.restcontroller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.SearchCondition;
import world.ssafy.tourtalk.model.service.AttractionService;

@WebMvcTest(AttractionRestController.class)
public class AttractionRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttractionService attractionService;

    private Attraction sampleAttraction;
    private List<Attraction> attractionList;
    private Page<Attraction> attractionPage;
    private List<Map<String, Object>> contentList;
    private List<Map<String, Object>> sidoList;

    @BeforeEach
    void setUp() {
        // 샘플 관광지 데이터 생성
        sampleAttraction = Attraction.builder()
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
        attractionList.add(sampleAttraction);
        attractionList.add(Attraction.builder()
                .no(2)
                .contentId(5678)
                .title("두 번째 관광지")
                .contentTypeId(12)
                .contentTypeName("관광지")
                .sidoCode(1)
                .sido("서울")
                .gugunCode(2)
                .gugun("강남구")
                .latitude(37.5173)
                .longitude(127.0403)
                .firstImage1("http://example.com/image2.jpg")
                .addr("서울시 강남구 테스트로 456")
                .overview("두 번째 관광지 설명입니다.")
                .viewCnt(50)
                .build());

        // 페이지 객체 생성
        attractionPage = Page.<Attraction>builder()
                .content(attractionList)
                .pageNumber(1)
                .pageSize(10)
                .totalElements(2)
                .totalPages(1)
                .first(true)
                .last(true)
                .startPage(1)
                .endPage(1)
                .build();

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
    }

    @Test
    @DisplayName("ID로 관광지 조회 테스트")
    void getAttractionByNoTest() throws Exception {
        // Given
        when(attractionService.getAttractionByNo(1)).thenReturn(sampleAttraction);
        when(attractionService.getAttractionsByDirectCodes(anyInt(), anyInt(), anyInt()))
                .thenReturn(attractionList);

        // When & Then
        mockMvc.perform(get("/api/v1/attractions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attraction.no").value(1))
                .andExpect(jsonPath("$.attraction.title").value("테스트 관광지"))
                .andExpect(jsonPath("$.nearbyAttractions").isArray())
                .andExpect(jsonPath("$.nearbyAttractions.length()").value(1));
    }

    @Test
    @DisplayName("코드 기반 관광지 목록 조회 테스트")
    void getAttractionsByDirectCodesTest() throws Exception {
        // Given
        when(attractionService.getAttractionsByDirectCodesWithPaging(
                eq(12), eq(1), eq(1), eq(1), eq(10)))
                .thenReturn(attractionPage);

        // When & Then
        mockMvc.perform(get("/api/v1/attractions/codes")
                .param("contentTypeId", "12")
                .param("sidoCode", "1")
                .param("gugunCode", "1")
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("검색 조건으로 관광지 조회 테스트")
    void searchAttractionsTest() throws Exception {
        // Given
        when(attractionService.searchAttractionsByCodes(any(SearchCondition.class)))
                .thenReturn(attractionPage);

        // When & Then
        mockMvc.perform(get("/api/v1/attractions/search")
                .param("contentTypeId", "12")
                .param("sidoCode", "1")
                .param("keyword", "테스트")
                .param("pageNumber", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("폼 데이터 조회 테스트")
    void getAttractionFormDataTest() throws Exception {
        // Given
        when(attractionService.getContent()).thenReturn(contentList);
        when(attractionService.getSido()).thenReturn(sidoList);
        when(attractionService.getRandomAttractions(6)).thenReturn(attractionList);

        // When & Then
        mockMvc.perform(get("/api/v1/attractions/form-data")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인기 관광지 목록 조회 테스트")
    void getPopularAttractionsTest() throws Exception {
        // Given
        List<Map<String, Object>> popularList = new ArrayList<>();
        Map<String, Object> popular = new HashMap<>();
        popular.put("no", 1);
        popular.put("title", "테스트 관광지");
        popular.put("sido_name", "서울");
        popular.put("view_cnt", 100);
        popularList.add(popular);

        when(attractionService.allCountView()).thenReturn(popularList);

        // When & Then
        mockMvc.perform(get("/api/v1/attractions/popular")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}