package world.ssafy.tourtalk.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.SearchCondition;

@Mapper
public interface AttractionMapper {
    
    // 기본 카테고리 및 코드 조회
    List<Map<String, Object>> getContent();
    List<Map<String, Object>> getSido();
    List<Map<String, Object>> getGugun(@Param("code") String code);
    
    // 단일 관광지 조회
    Attraction getAttractionByNo(@Param("no") int no);
    
    // 조회수 관련
    void updateViewCount(@Param("no") int no);
    List<Map<String, Object>> allCountView();
    
    // 코드 기반 직접 조회 메서드
    List<Attraction> getAttractionByDirectCodes(
            @Param("contentTypeId") int contentTypeId, 
            @Param("sidoCode") int sidoCode, 
            @Param("gugunCode") int gugunCode);
    
    // 페이징 처리가 포함된 코드 기반 조회
    List<Attraction> getAttractionByDirectCodesWithPaging(
            @Param("contentTypeId") int contentTypeId, 
            @Param("sidoCode") int sidoCode, 
            @Param("gugunCode") int gugunCode, 
            @Param("offset") int offset, 
            @Param("limit") int limit);
    
    // 총 항목 수 조회
    int getTotalCountByDirectCodes(
            @Param("contentTypeId") int contentTypeId, 
            @Param("sidoCode") int sidoCode, 
            @Param("gugunCode") int gugunCode);
    
    // SearchCondition을 활용한 동적 검색
    List<Attraction> searchAttractionsByCodes(SearchCondition condition);
    
    // 검색 결과의 총 항목 수
    int countAttractionsByCodes(SearchCondition condition);
    
    // 랜덤 관광지 조회
    List<Attraction> getRandomAttractions(
            @Param("count") int count, 
            @Param("contentTypeId") Integer contentTypeId, 
            @Param("sidoCode") Integer sidoCode);
    
    // 관광지 생성/수정/삭제 (관리자용)
    int insertAttraction(Attraction attraction);
    int updateAttraction(Attraction attraction);
    int deleteAttraction(@Param("no") int no);
    
    // 관광지 존재 여부 확인 (중복 체크용)
    int countAttractionByTitle(@Param("title") String title);
    
    // 컨텐츠 타입 존재 여부 확인
    int countContentTypeById(@Param("contentTypeId") int contentTypeId);
    
    // 시도 존재 여부 확인  
    int countSidoByCode(@Param("areaCode") int areaCode);
    
    // 구군 존재 여부 확인
    int countGugunByCode(@Param("siGunGuCode") int siGunGuCode);
    
    // 관광지명 자동완성
	List<Map<String, Integer>> findTitlesByKeyword(String string);
}