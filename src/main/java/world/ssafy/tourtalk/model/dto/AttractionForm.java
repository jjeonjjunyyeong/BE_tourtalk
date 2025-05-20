package world.ssafy.tourtalk.model.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttractionForm{
    private final List<Map<String, Object>> contentList;    // 컨텐츠 유형 목록
    private final List<Map<String, Object>> sidoList;       // 시도 목록
    private final List<Attraction> randomAttractions;       // 랜덤 추천 관광지 목록
}