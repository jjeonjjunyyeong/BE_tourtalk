package world.ssafy.tourtalk.model.dto.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.AttractionForm;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionFormResponseDto {
    private List<Map<String, Object>> contentList;    // 컨텐츠 유형 목록
    private List<Map<String, Object>> sidoList;       // 시도 목록
    private List<AttractionResponseDto> randomAttractions;  // 랜덤 추천 관광지 목록
    
    // AttractionForm으로부터 변환하는 정적 팩토리 메서드
    public static AttractionFormResponseDto from(AttractionForm form) {
        List<AttractionResponseDto> responseDtos = form.getRandomAttractions()
                .stream()
                .map(AttractionResponseDto::from)
                .collect(Collectors.toList());
                
        return AttractionFormResponseDto.builder()
                .contentList(form.getContentList())
                .sidoList(form.getSidoList())
                .randomAttractions(responseDtos)
                .build();
    }
}