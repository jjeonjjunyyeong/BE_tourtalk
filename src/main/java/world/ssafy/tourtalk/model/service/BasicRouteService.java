package world.ssafy.tourtalk.model.service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.KakaoRouteApiResponse;
import world.ssafy.tourtalk.model.dto.request.route.RouteRequestDto;
import world.ssafy.tourtalk.model.dto.request.route.RouteValidationDto;
import world.ssafy.tourtalk.model.dto.response.common.Coordinate;
import world.ssafy.tourtalk.model.dto.response.route.RouteResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicRouteService implements RouteService {
    
    private final RestTemplate restTemplate;
    
    @Value("${kakao.api.rest-key}")
    private String kakaoRestApiKey;
    
    @Value("${kakao.mobility.base-url}")
    private String kakaoMobilityBaseUrl;
    
    @Override
    public RouteResponseDto getRoute(RouteRequestDto requestDto) throws Exception {
        try {
            // 1. 요청 데이터 검증
            RouteValidationDto validation = RouteValidationDto.validate(requestDto);
            if (!validation.isValid()) {
                throw new IllegalArgumentException("잘못된 경로 요청: " + 
                    String.join(", ", validation.getErrors()));
            }
            
            // 2. API 요청 URL 구성
            URI uri = buildRouteApiUri(requestDto);
            
            // 3. HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 4. API 호출
            log.info("Kakao Mobility API 호출: {}", uri);
            ResponseEntity<KakaoRouteApiResponse> response = restTemplate.exchange(
                uri, HttpMethod.GET, entity, KakaoRouteApiResponse.class
            );
            
            // 5. 응답 처리
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return convertToRouteResponse(response.getBody(), requestDto);
            } else {
                throw new RuntimeException("Kakao Mobility API 호출 실패: " + response.getStatusCode());
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("경로 요청 검증 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("경로 검색 실패", e);
            throw new Exception("경로를 찾을 수 없습니다: " + e.getMessage());
        }
    }
    
    private URI buildRouteApiUri(RouteRequestDto requestDto) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(kakaoMobilityBaseUrl + "/v1/waypoints/directions");
        
        // 출발지
        builder.queryParam("origin", 
            requestDto.getOrigin().getX() + "," + requestDto.getOrigin().getY());
        
        // 목적지  
        builder.queryParam("destination",
            requestDto.getDestination().getX() + "," + requestDto.getDestination().getY());
        
        // 경유지 (있는 경우)
        if (requestDto.getWaypoints() != null && !requestDto.getWaypoints().isEmpty()) {
            String waypoints = requestDto.getWaypoints().stream()
                .map(point -> point.getX() + "," + point.getY())
                .collect(Collectors.joining("|"));
            builder.queryParam("waypoints", waypoints);
        }
        
        // 경로 옵션들
        builder.queryParam("priority", requestDto.getPriority());
        builder.queryParam("car_fuel", requestDto.getCarFuel());
        builder.queryParam("car_hipass", requestDto.getCarHipass().toString());
        builder.queryParam("alternatives", requestDto.getAlternatives().toString());
        builder.queryParam("road_details", requestDto.getRoadDetails().toString());
        
        return builder.build().toUri();
    }
    
    private RouteResponseDto convertToRouteResponse(KakaoRouteApiResponse kakaoResponse, 
                                                  RouteRequestDto originalRequest) {
        if (kakaoResponse.getRoutes() == null || kakaoResponse.getRoutes().isEmpty()) {
            throw new RuntimeException("경로 정보가 없습니다.");
        }
        
        KakaoRouteApiResponse.Route route = kakaoResponse.getRoutes().get(0);
        
        // 결과 코드 확인
        if (route.getResultCode() != 0) {
            throw new RuntimeException("경로 검색 실패: " + route.getResultMsg());
        }
        
        // 요약 정보 변환
        RouteResponseDto.RouteInfo routeInfo = RouteResponseDto.RouteInfo.builder()
            .totalDistance(route.getSummary().getDistance())
            .totalTime(route.getSummary().getDuration())
            .tollFare(route.getSummary().getFare() != null ? route.getSummary().getFare().getToll() : 0)
            .taxiFare(route.getSummary().getFare() != null ? route.getSummary().getFare().getTaxi() : 0)
            .origin(originalRequest.getOrigin())
            .destination(originalRequest.getDestination())
            .waypoints(originalRequest.getWaypoints())
            .build();
        
        // 구간 정보 변환
        List<RouteResponseDto.RouteSection> sections = new ArrayList<>();
        List<Coordinate> allCoordinates = new ArrayList<>();
        
        if (route.getSections() != null) {
            for (KakaoRouteApiResponse.Route.Section section : route.getSections()) {
                List<Coordinate> sectionCoords = new ArrayList<>();
                
                if (section.getRoads() != null) {
                    for (KakaoRouteApiResponse.Route.Road road : section.getRoads()) {
                        if (road.getVertexes() != null) {
                            for (List<Double> vertex : road.getVertexes()) {
                                if (vertex.size() >= 2) {
                                    Coordinate coord = Coordinate.builder()
                                        .longitude(BigDecimal.valueOf(vertex.get(0))) // x = 경도
                                        .latitude(BigDecimal.valueOf(vertex.get(1)))  // y = 위도
                                        .build();
                                    sectionCoords.add(coord);
                                    allCoordinates.add(coord);
                                }
                            }
                        }
                    }
                }
                
                RouteResponseDto.RouteSection routeSection = RouteResponseDto.RouteSection.builder()
                    .distance(section.getDistance())
                    .duration(section.getDuration())
                    .roads(sectionCoords)
                    .description(generateSectionDescription(section))
                    .trafficState(getAverageTrafficState(section))
                    .build();
                
                sections.add(routeSection);
            }
        }
        
        return RouteResponseDto.builder()
            .routeInfo(routeInfo)
            .sections(sections)
            .coordinates(allCoordinates)
            .build();
    }
    
    /**
     * 구간 설명 생성
     */
    private String generateSectionDescription(KakaoRouteApiResponse.Route.Section section) {
        if (section.getRoads() == null || section.getRoads().isEmpty()) {
            return "구간 정보 없음";
        }
        
        // 주요 도로명들을 수집
        List<String> roadNames = section.getRoads().stream()
            .map(KakaoRouteApiResponse.Route.Road::getName)
            .filter(name -> name != null && !name.trim().isEmpty())
            .distinct()
            .limit(3) // 최대 3개 도로명만
            .collect(Collectors.toList());
        
        if (roadNames.isEmpty()) {
            return String.format("%.1fkm 구간", section.getDistance() / 1000.0);
        }
        
        return String.join(" → ", roadNames);
    }
    
    /**
     * 구간의 평균 교통 상황 계산
     */
    private Integer getAverageTrafficState(KakaoRouteApiResponse.Route.Section section) {
        if (section.getRoads() == null || section.getRoads().isEmpty()) {
            return 0; // 원활
        }
        
        double avgTrafficState = section.getRoads().stream()
            .filter(road -> road.getTrafficState() != null)
            .mapToInt(KakaoRouteApiResponse.Route.Road::getTrafficState)
            .average()
            .orElse(0.0);
        
        return (int) Math.round(avgTrafficState);
    }
}
