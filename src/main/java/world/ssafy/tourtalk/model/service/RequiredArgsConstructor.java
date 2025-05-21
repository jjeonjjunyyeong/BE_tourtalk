package world.ssafy.tourtalk.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.RouteDay;
import world.ssafy.tourtalk.model.dto.RoutePlace;
import world.ssafy.tourtalk.model.dto.RouteTransport;
import world.ssafy.tourtalk.model.dto.request.route.RouteRecommendRequestDto;
import world.ssafy.tourtalk.model.dto.response.route.RouteResponseDto;
import world.ssafy.tourtalk.model.mapper.RouteMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
    private final RouteMapper routeMapper;
    private final AttractionService attractionService;
    private final DistanceCalculationService distanceService;
    
    private static final int DEFAULT_VISIT_TIME = 60; // 기본 방문 시간 (분)
    
    @Override
    @Transactional
    public RouteResponseDto recommendRoute(Integer mno, RouteRecommendRequestDto requestDto) {
        log.info("경로 추천 요청: attractionIds={}, optimizeBy={}", 
                requestDto.getAttractionIds(), requestDto.getOptimizeBy());
        
        // 1. 관광지 정보 조회
        List<Attraction> attractions = requestDto.getAttractionIds().stream()
                .map(attractionService::getAttractionByNo)
                .collect(Collectors.toList());
        
        if (attractions.isEmpty()) {
            throw new IllegalArgumentException("유효한 관광지가 없습니다");
        }
        
        // 2. 최적 경로 계산
        List<Integer> optimizedOrder = optimizeRoute(attractions, requestDto.getOptimizeBy());
        
        // 3. 일자별 일정 계획 수립
        List<RouteDayDto> days = createDailySchedule(
                attractions, 
                optimizedOrder, 
                requestDto.getStartTime(), 
                requestDto.getEndTime(), 
                requestDto.getMaxDays(),
                requestDto.getTransportType()
        );
        
        // 4. 결과 저장 및 반환
        // 4.1. 경로 기본 정보 저장
        TravelRoute route = TravelRoute.builder()
                .mno(mno)
                .title(requestDto.getTitle() != null ? requestDto.getTitle() : generateRouteTitle(attractions))
                .description(requestDto.getDescription())
                .isPublic(false)
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .build();
        
        routeMapper.insertRoute(route);
        Integer routeId = route.getRouteId();
        
        // 4.2. 일자 정보 저장
        for (int i = 0; i < days.size(); i++) {
            RouteDayDto dayDto = days.get(i);
            
            RouteDay day = RouteDay.builder()
                    .routeId(routeId)
                    .dayNumber(i + 1)
                    .date(dayDto.getDate())
                    .dayTotalTime(dayDto.getDayTotalTime())
                    .build();
            
            routeMapper.insertRouteDay(day);
            Integer dayId = day.getDayId();
            
            // 4.3. 장소 정보 저장
            for (RoutePlaceDto placeDto : dayDto.getPlaces()) {
                RoutePlace place = RoutePlace.builder()
                        .routeId(routeId)
                        .attractionNo(placeDto.getAttraction().getNo())
                        .visitOrder(placeDto.getVisitOrder())
                        .estimatedTime(placeDto.getEstimatedTime())
                        .memo(placeDto.getMemo())
                        .build();
                
                routeMapper.insertRoutePlace(place);
                Integer placeId = place.getPlaceId();
                
                // 일자-장소 매핑 저장
                routeMapper.insertRouteDayPlace(dayId, placeId);
                
                // 4.4. 이동 정보 저장 (첫 번째 장소 제외)
                if (placeDto.getTransport() != null) {
                    RouteTransport transport = RouteTransport.builder()
                            .routeId(routeId)
                            .fromPlaceId(findPreviousPlaceId(dayDto, placeDto))
                            .toPlaceId(placeId)
                            .transportType(placeDto.getTransport().getTransportType())
                            .distance(placeDto.getTransport().getDistance())
                            .estimatedTime(placeDto.getTransport().getEstimatedTime())
                            .description(placeDto.getTransport().getDescription())
                            .build();
                    
                    routeMapper.insertRouteTransport(transport);
                }
            }
        }
        
        // 5. 응답 DTO 작성
        return getRouteById(routeId);
    }
    
    /**
     * 이전 장소 ID 찾기
     */
    private Integer findPreviousPlaceId(RouteDayDto day, RoutePlaceDto place) {
        for (RoutePlaceDto p : day.getPlaces()) {
            if (p.getVisitOrder() == place.getVisitOrder() - 1) {
                return p.getPlaceId();
            }
        }
        return null;
    }
    
    /**
     * 경로 제목 자동 생성
     */
    private String generateRouteTitle(List<Attraction> attractions) {
        // 첫 번째와 마지막 관광지로 제목 생성
        if (attractions.size() >= 2) {
            return attractions.get(0).getTitle() + " → " + 
                   attractions.get(attractions.size() - 1).getTitle() + " 여행";
        } else {
            return attractions.get(0).getTitle() + " 여행";
        }
    }
    
    /**
     * 최적 경로 계산 (TSP 알고리즘)
     */
    private List<Integer> optimizeRoute(List<Attraction> attractions, String optimizeBy) {
        int n = attractions.size();
        if (n <= 2) {
            // 관광지가 2개 이하면 순서 최적화 불필요
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                result.add(i);
            }
            return result;
        }
        
        // 거리 행렬 계산 (도착지-출발지 행렬)
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 0; // 자기 자신
                } else {
                    Attraction from = attractions.get(i);
                    Attraction to = attractions.get(j);
                    
                    // 최적화 기준에 따라 비용 계산
                    if ("DISTANCE".equals(optimizeBy)) {
                        matrix[i][j] = calculateHaversineDistance(
                                from.getLatitude(), from.getLongitude(),
                                to.getLatitude(), to.getLongitude());
                    } else if ("TIME".equals(optimizeBy)) {
                        // 거리에 이동 시간 가중치 추가
                        double distance = calculateHaversineDistance(
                                from.getLatitude(), from.getLongitude(),
                                to.getLatitude(), to.getLongitude());
                        matrix[i][j] = distance;
                    } else if ("RATING".equals(optimizeBy)) {
                        // 거리와 인기도 함께 고려
                        double distance = calculateHaversineDistance(
                                from.getLatitude(), from.getLongitude(),
                                to.getLatitude(), to.getLongitude());
                        double viewCountFactor = 1.0 - Math.min(1.0, to.getViewCnt() / 1000.0);
                        matrix[i][j] = distance * viewCountFactor;
                    } else {
                        matrix[i][j] = calculateHaversineDistance(
                                from.getLatitude(), from.getLongitude(),
                                to.getLatitude(), to.getLongitude());
                    }
                }
            }
        }
        
        // 최적 경로 계산 (간단한 근사 TSP 구현 - Nearest Neighbor + 2-opt)
        List<Integer> route = nearestNeighborTSP(matrix);
        route = twoOptImprovement(route, matrix);
        
        return route;
    }
    
    /**
     * Nearest Neighbor 알고리즘 (그리디 방식의 TSP 근사해)
     */
    private List<Integer> nearestNeighborTSP(double[][] matrix) {
        int n = matrix.length;
        List<Integer> route = new ArrayList<>();
        boolean[] visited = new boolean[n];
        
        // 첫 번째 도시를 시작점으로
        int currentCity = 0;
        route.add(currentCity);
        visited[currentCity] = true;
        
        // 남은 n-1개 도시 추가
        for (int i = 1; i < n; i++) {
            int nextCity = findNearestCity(currentCity, visited, matrix);
            route.add(nextCity);
            visited[nextCity] = true;
            currentCity = nextCity;
        }
        
        return route;
    }
    
    /**
     * 현재 도시에서 가장 가까운 미방문 도시 찾기
     */
    private int findNearestCity(int currentCity, boolean[] visited, double[][] matrix) {
        int n = matrix.length;
        int nearestCity = -1;
        double minDistance = Double.MAX_VALUE;
        
        for (int i = 0; i < n; i++) {
            if (!visited[i] && matrix[currentCity][i] < minDistance) {
                nearestCity = i;
                minDistance = matrix[currentCity][i];
            }
        }
        
        return nearestCity;
    }
    
    /**
     * 2-opt 개선 알고리즘 적용
     */
    private List<Integer> twoOptImprovement(List<Integer> route, double[][] matrix) {
        int n = route.size();
        boolean improved = true;
        
        while (improved) {
            improved = false;
            double bestDistance = calculateRouteDistance(route, matrix);
            
            for (int i = 0; i < n - 2; i++) {
                for (int j = i + 2; j < n; j++) {
                    // i와 j 사이 구간 뒤집기
                    List<Integer> newRoute = new ArrayList<>(route);
                    reverse(newRoute, i + 1, j);
                    
                    double newDistance = calculateRouteDistance(newRoute, matrix);
                    if (newDistance < bestDistance) {
                        route = newRoute;
                        bestDistance = newDistance;
                        improved = true;
                        break;
                    }
                }
                if (improved) {
                    break;
                }
            }
        }
        
        return route;
    }
    
    /**
     * 리스트 특정 구간 뒤집기
     */
    private void reverse(List<Integer> route, int start, int end) {
        while (start < end) {
            int temp = route.get(start);
            route.set(start, route.get(end));
            route.set(end, temp);
            start++;
            end--;
        }
    }
    
    /**
     * 총 경로 거리 계산
     */
    private double calculateRouteDistance(List<Integer> route, double[][] matrix) {
        double totalDistance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            totalDistance += matrix[route.get(i)][route.get(i + 1)];
        }
        return totalDistance;
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
                * Math.sin(