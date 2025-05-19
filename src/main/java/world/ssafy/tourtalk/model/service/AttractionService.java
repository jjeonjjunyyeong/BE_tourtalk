package world.ssafy.tourtalk.model.service;

import java.util.List;
import java.util.Map;

import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.SearchCondition;
import world.ssafy.tourtalk.model.dto.request.attraction.AttractionCreateUpdateRequestDto;

public interface AttractionService {
    // 기본 조회 메서드
    List<Map<String, Object>> getContent();
    List<Map<String, Object>> getSido();
    List<Map<String, Object>> getGugun(String code);
    Attraction getAttractionByNo(int no);
    
    // 이름으로 코드 조회 메서드
    int getContentTypeIdByName(String contentTypeName);
    int getSidoCodeByName(String sidoName);
    int getGugunCodeByName(String gugunName, int sidoCode);
    
    // 랜덤 관광지 조회
    List<Attraction> getRandomAttractions(int count);
    List<Attraction> getRandomAttractionsByTheme(int count, int contentTypeId);
    List<Attraction> getRandomAttractionsByRegion(int count, int sidoCode);
    
    // 조회수 관련
    void updateViewCount(int no);
    List<Map<String, Object>> allCountView();
    
    // 코드 값 기반 직접 조회
    List<Attraction> getAttractionsByDirectCodes(int contentTypeId, int sidoCode, int gugunCode);
    Page<Attraction> getAttractionsByDirectCodesWithPaging(int contentTypeId, int sidoCode, int gugunCode, int pageNumber, int pageSize);
    
    // SearchCondition 기반 검색
    Page<Attraction> searchAttractionsByCodes(SearchCondition condition);
    
    // 관광지 생성/수정/삭제 (관리자용)
    Attraction createAttraction(AttractionCreateUpdateRequestDto requestDto);
    Attraction updateAttraction(int no, AttractionCreateUpdateRequestDto requestDto);
    boolean deleteAttraction(int no);
    
    // 관광지 존재 여부 확인
    boolean existsAttractionByTitle(String title);
    
    // 참조 데이터 존재 여부 확인
    boolean validateReferences(int contentTypeId, int areaCode, int siGunGuCode);
}