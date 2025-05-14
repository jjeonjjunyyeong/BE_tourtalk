package world.ssafy.tourtalk.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.AttractionDetail;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AttractionDetailResponseDto {
    private AttractionResponseDto attraction;
    private AttractionResponseDto[] nearbyAttractions;
    
    // AttractionDetail로부터 변환하는 정적 팩토리 메서드
    public static AttractionDetailResponseDto from(AttractionDetail detail) {
        AttractionResponseDto mainAttraction = AttractionResponseDto.from(detail.getAttraction());
        
        Attraction[] originals = detail.getNearbyAttractions();
        AttractionResponseDto[] nearbyAttractions = new AttractionResponseDto[originals.length];
        
        for (int i = 0; i < originals.length; i++) {
            nearbyAttractions[i] = AttractionResponseDto.from(originals[i]);
        }
        
        return AttractionDetailResponseDto.builder()
                .attraction(mainAttraction)
                .nearbyAttractions(nearbyAttractions)
                .build();
    }
}