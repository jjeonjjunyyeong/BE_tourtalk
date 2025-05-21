package world.ssafy.tourtalk.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistanceCalculationService {
    private final RestTemplate restTemplate;
    
    @Value("${api.kakao.mobility.key}")
    private String kakaoMobilityApiKey;
    
    /**
     * 카카오 모빌리티 API를 사용한 실제 도로 거리 및 시간 계산
     * @return Map 형태의 결과 [distance: 미터, duration: 초]
     */
    public Map<String, Integer> calculateDistanceAndTime(
            double fromLat, double fromLng, double toLat, double toLng, String transportType) {
        try {
            String url = "https://apis-navi.kakaomobility.com/v1/directions";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoMobilityApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 이동 수단에 따른 옵션 설정
            String priority = "RECOMMEND";
            if ("WALK".equals(transportType)) {
                priority = "WALKTIME";
            } else if ("BUS".equals(transportType) || "SUBWAY".equals(transportType)) {
                priority = "TRANSIT";
            }
            
            Map<String, Object> requestBody = Map.of(
                "origin", fromLng + "," + fromLat,
                "destination", toLng + "," + toLat,
                "waypoints", "",
                "priority", priority,
                "car_type", "1",
                "options", "trafast"
            );
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            
            // 응답에서 거리/시간 정보 추출
            List<Map<String, Object>> routes = (List<Map<String, Object>>) responseBody.get("routes");
            Map<String, Object> route = routes.get(0);
            Map<String, Object> summary = (Map<String, Object>) route.get("summary");
            
            int distance = (Integer) summary.get("distance"); // 미터 단위
            int duration = (Integer) summary.get("duration"); // 초 단위
            
            return Map.of("distance", distance, "duration", duration);
            
        } catch (Exception e) {
            log.error("거리 계산 API 호출 실패: {}", e.getMessage());
            // API 호출 실패 시 근사값으로 대체
            int distance = (int) (calculateHaversineDistance(fromLat, fromLng, toLat, toLng) * 1000); // m
            int duration;
            
            // 이동 수단별 예상 속도 계산 (km/h)
            double speedKmh;
            if ("WALK".equals(transportType)) {
                speedKmh = 4.0; // 도보 평균 속도
            } else if ("BICYCLE".equals(transportType)) {
                speedKmh = 15.0; // 자전거 평균 속도
            } else if ("BUS".equals(transportType) || "SUBWAY".equals(transportType)) {
                speedKmh = 25.0; // 대중교통 평균 속도
            } else {
                speedKmh = 40.0; // 자동차 평균 속도
            }
            
            // 시간 계산 (초)
            duration = (int) (distance / speedKmh * 3.6);
            
            return Map.of("distance", distance, "duration", duration);
        }
    }
    
    /**
     * 하버사인 공식을 사용한 직선 거리 계산 (km)
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // 지구 반경 (km)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}