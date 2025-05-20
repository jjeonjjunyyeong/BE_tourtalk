package world.ssafy.tourtalk.model.dto.response.attraction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.AttractionDetail;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionDetailResponseDto {
    private AttractionResponseDto attraction;
    private List<AttractionResponseDto> nearbyAttractions;
    
    // AttractionDetail 객체로부터 응답 DTO 생성
    public static AttractionDetailResponseDto from(AttractionDetail detail) {
        AttractionResponseDto mainAttraction = AttractionResponseDto.from(detail.getAttraction());
        
        List<AttractionResponseDto> nearby = Arrays.stream(detail.getNearbyAttractions())
                .map(AttractionResponseDto::from)
                .collect(Collectors.toList());
        
        return AttractionDetailResponseDto.builder()
                .attraction(mainAttraction)
                .nearbyAttractions(nearby)
                .build();
    }
}