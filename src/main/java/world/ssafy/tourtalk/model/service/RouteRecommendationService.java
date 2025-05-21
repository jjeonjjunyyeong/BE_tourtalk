package world.ssafy.tourtalk.model.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.Attraction;

@Service
@RequiredArgsConstructor
public class RouteRecommendationService {
    private final AttractionService attractionService;
    private final RouteRepository routeRepository;
    private final DistanceCalculationService distanceService;
    
    /**
     * TSP(Traveling Salesman Problem) 알고리즘을 활용한 관광지 순서 최적화
     */
    public List<Integer> optimizeRoute(List<Integer> attractionIds, String optimizeBy) {
        // 관광지 정보 조회
        List<Attraction> attractions = attractionIds.stream()
                .map(attractionService::getAttractionByNo)
                .collect(Collectors.toList());
        
        // 거리 행렬 계산
        double[][] distanceMatrix = calculateDistanceMatrix(attractions);
        
        // 최적 경로 계산 (그리디 알고리즘, 2-opt 개선 등)
        return findOptimalRoute(attractions, distanceMatrix, optimizeBy);
    }
    
    /**
     * 관광지 간 거리 행렬 계산
     */
    private double[][] calculateDistanceMatrix(List<Attraction> attractions) {
        int n = attractions.size();
        double[][] distances = new double[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distances[i][j] = 0;
                } else {
                    Attraction a1 = attractions.get(i);
                    Attraction a2 = attractions.get(j);
                    // 실제 도로 거리 계산 (API 활용) 또는 근사값으로 직선 거리 계산
                    distances[i][j] = distanceService.calculateDistance(
                            a1.getLatitude(), a1.getLongitude(),
                            a2.getLatitude(), a2.getLongitude());
                }
            }
        }
        return distances;
    }
    
    /**
     * 최적 경로 찾기 (그리디 + 2-opt 개선)
     */
    private List<Integer> findOptimalRoute(List<Attraction> attractions, 
                                         double[][] distanceMatrix, 
                                         String optimizeBy) {
        // 1. 초기 경로 생성 (Nearest Neighbor)
        List<Integer> route = createInitialRoute(attractions.size(), distanceMatrix);
        
        // 2. 2-opt 알고리즘으로 경로 개선
        route = improveTwoOpt(route, distanceMatrix);
        
        // 3. 최적화 기준에 따른 추가 조정
        if ("RATING".equals(optimizeBy)) {
            route = adjustRouteByRating(route, attractions);
        } else if ("TIME".equals(optimizeBy)) {
            route = adjustRouteByTime(route, attractions, distanceMatrix);
        }
        
        // 원래 관광지 ID로 매핑하여 반환
        return route.stream()
                .map(idx -> attractions.get(idx).getNo())
                .collect(Collectors.toList());
    }
    
    /**
     * 일자별 일정 계획 수립
     */
    public List<RouteDayDto> createDailySchedule(List<Attraction> attractions, 
                                              List<Integer> optimizedOrder,
                                              LocalTime startTime, 
                                              LocalTime endTime, 
                                              Integer maxDays) {
        int dailyAvailableMinutes = (int) Duration.between(startTime, endTime).toMinutes();
        List<RouteDayDto> days = new ArrayList<>();
        
        LocalDate currentDate = LocalDate.now();
        List<RoutePlaceDto> currentDayPlaces = new ArrayList<>();
        int currentDayTime = 0;
        int dayIndex = 1;
        
        for (Integer idx : optimizedOrder) {
            Attraction attraction = attractions.get(idx);
            int visitTime = attraction.getAvgVisitTime() != null ? attraction.getAvgVisitTime() : 60;
            
            // 이동 시간 계산 (이전 장소가 있는 경우)
            int travelTime = 0;
            if (!currentDayPlaces.isEmpty()) {
                RoutePlaceDto lastPlace = currentDayPlaces.get(currentDayPlaces.size() - 1);
                Attraction lastAttraction = attractions.get(optimizedOrder.indexOf(lastPlace.getVisitOrder() - 1));
                
                travelTime = calculateTravelTime(
                    lastAttraction.getLatitude(), lastAttraction.getLongitude(),
                    attraction.getLatitude(), attraction.getLongitude());
            }
            
            // 현재 일자에 추가 가능한지 확인
            if (currentDayTime + travelTime + visitTime <= dailyAvailableMinutes) {
                // 현재 일자에 추가
                RoutePlaceDto place = createRoutePlaceDto(attraction, currentDayPlaces.size() + 1, visitTime);
                if (travelTime > 0) {
                    place.setTransport(createTransportDto(travelTime));
                }
                currentDayPlaces.add(place);
                currentDayTime += (travelTime + visitTime);
            } else {
                // 새 일자 시작
                if (!currentDayPlaces.isEmpty()) {
                    days.add(createRouteDayDto(currentDate, currentDayPlaces, currentDayTime));
                    currentDayPlaces = new ArrayList<>();
                    currentDayTime = 0;
                    currentDate = currentDate.plusDays(1);
                    dayIndex++;
                }
                
                // 최대 일수 제한 체크
                if (maxDays != null && dayIndex > maxDays) {
                    break;
                }
                
                // 새 일자에 현재 장소 추가
                RoutePlaceDto place = createRoutePlaceDto(attraction, 1, visitTime);
                currentDayPlaces.add(place);
                currentDayTime += visitTime;
            }
        }
        
        // 마지막 일자 추가
        if (!currentDayPlaces.isEmpty()) {
            days.add(createRouteDayDto(currentDate, currentDayPlaces, currentDayTime));
        }
        
        return days;
    }
    
    // 헬퍼 메서드들...
}