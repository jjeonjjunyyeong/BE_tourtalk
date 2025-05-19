package world.ssafy.tourtalk.model.dto.response.attraction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.AttractionForm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionFormResponseDto {
    private List<Map<String, Object>> contentList;
    private List<Map<String, Object>> sidoList;
    private List<AttractionResponseDto> randomAttractions;
    
    // AttractionForm 객체로부터 응답 DTO 생성
    public static AttractionFormResponseDto from(AttractionForm form) {
        List<AttractionResponseDto> randomAttractionResponses = form.getRandomAttractions().stream()
                .map(AttractionResponseDto::from)
                .collect(Collectors.toList());
        
        return AttractionFormResponseDto.builder()
                .contentList(form.getContentList())
                .sidoList(form.getSidoList())
                .randomAttractions(randomAttractionResponses)
                .build();
    }
}