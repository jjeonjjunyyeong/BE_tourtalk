package world.ssafy.tourtalk.model.dto.request.route;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import world.ssafy.tourtalk.model.dto.response.common.Coordinate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteValidationDto {
    
    /**
     * 검증 결과
     */
    private boolean valid;
    
    /**
     * 오류 메시지 목록
     */
    private List<String> errors;
    
    /**
     * 경로 요청이 유효한지 검증
     */
    public static RouteValidationDto validate(RouteRequestDto request) {
        RouteValidationDto validation = new RouteValidationDto();
        validation.valid = true;
        validation.errors = new java.util.ArrayList<>();
        
        // 출발지 검증
        if (request.getOrigin() == null) {
            validation.errors.add("출발지가 설정되지 않았습니다.");
            validation.valid = false;
        } else if (!request.getOrigin().isValid()) {
            validation.errors.add("출발지 좌표가 유효하지 않습니다.");
            validation.valid = false;
        }
        
        // 목적지 검증
        if (request.getDestination() == null) {
            validation.errors.add("목적지가 설정되지 않았습니다.");
            validation.valid = false;
        } else if (!request.getDestination().isValid()) {
            validation.errors.add("목적지 좌표가 유효하지 않습니다.");
            validation.valid = false;
        }
        
        // 출발지와 목적지가 같은지 검증
        if (request.getOrigin() != null && request.getDestination() != null &&
            request.getOrigin().equals(request.getDestination())) {
            validation.errors.add("출발지와 목적지가 동일합니다.");
            validation.valid = false;
        }
        
        // 경유지 검증
        if (request.getWaypoints() != null) {
            for (int i = 0; i < request.getWaypoints().size(); i++) {
                Coordinate waypoint = request.getWaypoints().get(i);
                if (waypoint == null || !waypoint.isValid()) {
                    validation.errors.add("경유지 " + (i + 1) + "의 좌표가 유효하지 않습니다.");
                    validation.valid = false;
                }
            }
            
            // 경유지 개수 제한 (Kakao API 제한)
            if (request.getWaypoints().size() > 3) {
                validation.errors.add("경유지는 최대 3개까지 설정할 수 있습니다.");
                validation.valid = false;
            }
        }
        
        return validation;
    }
}