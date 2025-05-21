package world.ssafy.tourtalk.model.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.exception.ResourceNotFoundException;
import world.ssafy.tourtalk.exception.UnauthorizedException;
import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.RouteDay;
import world.ssafy.tourtalk.model.dto.RoutePlace;
import world.ssafy.tourtalk.model.dto.RouteTransport;
import world.ssafy.tourtalk.model.dto.request.PageRequest;
import world.ssafy.tourtalk.model.dto.request.route.RouteCreateRequestDto;
import world.ssafy.tourtalk.model.dto.request.route.RoutePlaceRequestDto;
import world.ssafy.tourtalk.model.dto.request.route.RouteRecommendRequestDto;
import world.ssafy.tourtalk.model.dto.response.attraction.AttractionResponseDto;
import world.ssafy.tourtalk.model.dto.response.route.RouteResponseDto;
import world.ssafy.tourtalk.model.entity.*;
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
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * 일자별 일정 계획 수립
     */
    private List<RouteDayDto> createDailySchedule(
            List<Attraction> attractions, 
            List<Integer> optimizedOrder,
            LocalTime startTime, 
            LocalTime endTime, 
            Integer maxDays,
            String transportType) {
        
        int dailyAvailableMinutes = (int) Duration.between(startTime, endTime).toMinutes();
        List<RouteDayDto> days = new ArrayList<>();
        
        LocalDate currentDate = LocalDate.now();
        List<RoutePlaceDto> currentDayPlaces = new ArrayList<>();
        int currentDayTime = 0;
        int dayNumber = 1;
        int visitOrder = 1;
        
        for (int i = 0; i < optimizedOrder.size(); i++) {
            int idx = optimizedOrder.get(i);
            Attraction attraction = attractions.get(idx);
            
            // 방문 예상 시간 (분)
            int visitTime = attraction.getAvgVisitTime() != null ? 
                    attraction.getAvgVisitTime() : DEFAULT_VISIT_TIME;
            
            // 이동 정보 계산 (이전 장소가 있는 경우)
            RouteTransportDto transport = null;
            int travelTime = 0;
            int travelDistance = 0;
            
            if (!currentDayPlaces.isEmpty()) {
                RoutePlaceDto lastPlace = currentDayPlaces.get(currentDayPlaces.size() - 1);
                int lastIdx = findIndexByVisitOrder(lastPlace.getVisitOrder(), optimizedOrder);
                Attraction lastAttraction = attractions.get(lastIdx);
                
                // 거리 및 시간 계산
                Map<String, Integer> travelInfo = calculateTravel(
                        lastAttraction.getLatitude(), lastAttraction.getLongitude(),
                        attraction.getLatitude(), attraction.getLongitude(),
                        transportType);
                
                travelDistance = travelInfo.get("distance");
                travelTime = travelInfo.get("duration") / 60; // 초→분 변환
                
                // 이동 정보 DTO 생성
                transport = RouteTransportDto.builder()
                        .transportType(transportType)
                        .distance(travelDistance)
                        .estimatedTime(travelTime)
                        .description(generateTravelDescription(lastAttraction, attraction, transportType))
                        .build();
            }
            
            // 현재 일자에 추가 가능한지 확인
            if (currentDayTime + travelTime + visitTime <= dailyAvailableMinutes) {
                // 현재 일자에 추가
                RoutePlaceDto place = RoutePlaceDto.builder()
                        .attraction(AttractionResponseDto.from(attraction))
                        .visitOrder(visitOrder++)
                        .estimatedTime(visitTime)
                        .memo("")
                        .transport(transport)
                        .build();
                
                currentDayPlaces.add(place);
                currentDayTime += (travelTime + visitTime);
            } else {
                // 새 일자 시작
                if (!currentDayPlaces.isEmpty()) {
                    days.add(RouteDayDto.builder()
                            .dayNumber(dayNumber++)
                            .date(currentDate)
                            .places(new ArrayList<>(currentDayPlaces))
                            .dayTotalTime(currentDayTime)
                            .build());
                    
                    currentDayPlaces.clear();
                    currentDayTime = 0;
                    currentDate = currentDate.plusDays(1);
                    visitOrder = 1;
                }
                
                // 최대 일수 제한 체크
                if (maxDays != null && dayNumber > maxDays) {
                    break;
                }
                
                // 새 일자에 현재 장소 추가
                RoutePlaceDto place = RoutePlaceDto.builder()
                        .attraction(AttractionResponseDto.from(attraction))
                        .visitOrder(visitOrder++)
                        .estimatedTime(visitTime)
                        .memo("")
                        .transport(null) // 첫 장소는 이동 정보 없음
                        .build();
                
                currentDayPlaces.add(place);
                currentDayTime += visitTime;
            }
        }
        
        // 마지막 일자 추가
        if (!currentDayPlaces.isEmpty()) {
            days.add(RouteDayDto.builder()
                    .dayNumber(dayNumber)
                    .date(currentDate)
                    .places(currentDayPlaces)
                    .dayTotalTime(currentDayTime)
                    .build());
        }
        
        return days;
    }
    
    /**
     * 방문 순서로 인덱스 찾기
     */
    private int findIndexByVisitOrder(int visitOrder, List<Integer> optimizedOrder) {
        if (visitOrder <= optimizedOrder.size()) {
            return optimizedOrder.get(visitOrder - 1);
        }
        return -1;
    }
    
    /**
     * 거리 및 이동 시간 계산
     */
    private Map<String, Integer> calculateTravel(
            double fromLat, double fromLng, double toLat, double toLng, String transportType) {
        
        // 외부 거리 계산 서비스 호출
        return distanceService.calculateDistanceAndTime(
                fromLat, fromLng, toLat, toLng, transportType);
    }
    
    /**
     * 이동 설명 생성
     */
    private String generateTravelDescription(
            Attraction from, Attraction to, String transportType) {
        StringBuilder description = new StringBuilder();
        
        switch (transportType) {
            case "WALK":
                description.append("도보로 이동 (");
                description.append(from.getTitle());
                description.append(" → ");
                description.append(to.getTitle());
                description.append(")");
                break;
            case "CAR":
                description.append("자동차로 이동 (");
                if (from.getAddr() != null && to.getAddr() != null) {
                    description.append(extractAddressMainPart(from.getAddr()));
                    description.append(" → ");
                    description.append(extractAddressMainPart(to.getAddr()));
                } else {
                    description.append(from.getTitle());
                    description.append(" → ");
                    description.append(to.getTitle());
                }
                description.append(")");
                break;
            case "BUS":
                description.append("버스로 이동");
                break;
            case "SUBWAY":
                description.append("지하철로 이동");
                break;
            default:
                description.append(transportType).append("로 이동");
        }
        
        return description.toString();
    }
    
    /**
     * 주소에서 시군구 부분 추출
     */
    private String extractAddressMainPart(String fullAddress) {
        if (fullAddress == null) return "";
        
        // 시/도, 시/군/구 부분만 추출 (예: '서울특별시 종로구' 추출)
        String[] parts = fullAddress.split(" ");
        if (parts.length >= 2) {
            return parts[0] + " " + parts[1];
        }
        return fullAddress;
    }
    
    @Override
    @Transactional
    public RouteResponseDto createRoute(Integer mno, RouteCreateRequestDto requestDto) {
        log.info("경로 생성 요청: mno={}, title={}", mno, requestDto.getTitle());
        
        // 1. 경로 기본 정보 저장
        TravelRoute route = TravelRoute.builder()
                .mno(mno)
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .isPublic(requestDto.getIsPublic() != null ? requestDto.getIsPublic() : false)
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .build();
        
        routeMapper.insertRoute(route);
        Integer routeId = route.getRouteId();
        
        // 2. 장소별 날짜 그룹화 (일자별로 분류)
        Map<LocalDate, List<RoutePlaceRequestDto>> placesByDate = new HashMap<>();
        
        for (RoutePlaceRequestDto placeRequest : requestDto.getPlaces()) {
            LocalDate date = placeRequest.getVisitDate();
            if (date == null) {
                // 날짜가 없으면 시작일 또는 현재 날짜 사용
                date = requestDto.getStartDate() != null ? 
                        requestDto.getStartDate() : LocalDate.now();
            }
            
            placesByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(placeRequest);
        }
        
        // 3. 일자 정보 저장 및 장소 할당
        List<LocalDate> sortedDates = new ArrayList<>(placesByDate.keySet());
        Collections.sort(sortedDates);
        
        for (int i = 0; i < sortedDates.size(); i++) {
            LocalDate date = sortedDates.get(i);
            List<RoutePlaceRequestDto> dayPlaces = placesByDate.get(date);
            
            // 방문 순서로 정렬
            dayPlaces.sort(Comparator.comparing(RoutePlaceRequestDto::getVisitOrder));
            
            // 일자 정보 저장
            RouteDay day = RouteDay.builder()
                    .routeId(routeId)
                    .dayNumber(i + 1)
                    .date(date)
                    .dayTotalTime(calculateDayTotalTime(dayPlaces)) // 총 소요 시간 계산
                    .build();
            
            routeMapper.insertRouteDay(day);
            Integer dayId = day.getDayId();
            
            // 이전 장소 ID (이동 정보용)
            Integer prevPlaceId = null;
            
            // 장소 정보 저장
            for (RoutePlaceRequestDto placeRequest : dayPlaces) {
                // 관광지 정보 검증
                Attraction attraction = attractionService.getAttractionByNo(placeRequest.getAttractionNo());
                if (attraction == null) {
                    log.warn("존재하지 않는 관광지: {}", placeRequest.getAttractionNo());
                    continue;
                }
                
                RoutePlace place = RoutePlace.builder()
                        .routeId(routeId)
                        .attractionNo(placeRequest.getAttractionNo())
                        .visitOrder(placeRequest.getVisitOrder())
                        .estimatedTime(placeRequest.getEstimatedTime() != null ? 
                                placeRequest.getEstimatedTime() : DEFAULT_VISIT_TIME)
                        .visitDate(date)
                        .memo(placeRequest.getMemo())
                        .build();
                
                routeMapper.insertRoutePlace(place);
                Integer placeId = place.getPlaceId();
                
                // 일자-장소 매핑 저장
                routeMapper.insertRouteDayPlace(dayId, placeId);
                
                // 이동 정보 저장 (첫 번째 장소 제외)
                if (prevPlaceId != null && placeRequest.getTransportType() != null) {
                    RoutePlace prevPlace = routeMapper.getRoutePlaceById(prevPlaceId);
                    Attraction prevAttraction = attractionService.getAttractionByNo(prevPlace.getAttractionNo());
                    
                    Map<String, Integer> travelInfo = calculateTravel(
                            prevAttraction.getLatitude(), prevAttraction.getLongitude(),
                            attraction.getLatitude(), attraction.getLongitude(),
                            placeRequest.getTransportType());
                    
                    RouteTransport transport = RouteTransport.builder()
                            .routeId(routeId)
                            .fromPlaceId(prevPlaceId)
                            .toPlaceId(placeId)
                            .transportType(placeRequest.getTransportType())
                            .distance(travelInfo.get("distance"))
                            .estimatedTime(travelInfo.get("duration") / 60) // 초→분 변환
                            .description(generateTravelDescription(prevAttraction, attraction, placeRequest.getTransportType()))
                            .build();
                    
                    routeMapper.insertRouteTransport(transport);
                }
                
                prevPlaceId = placeId;
            }
        }
        
        // 4. 결과 조회 및 반환
        return getRouteById(routeId);
    }
    
    /**
     * 일자 총 소요 시간 계산
     */
    private int calculateDayTotalTime(List<RoutePlaceRequestDto> places) {
        return places.stream()
                .mapToInt(p -> p.getEstimatedTime() != null ? p.getEstimatedTime() : DEFAULT_VISIT_TIME)
                .sum();
    }
    
    @Override
    @Transactional(readOnly = true)
    public RouteResponseDto getRouteById(Integer routeId) {
        log.info("경로 조회 요청: routeId={}", routeId);
        
        // 1. 경로 기본 정보 조회
        TravelRoute route = routeMapper.getRouteById(routeId);
        if (route == null) {
            throw new ResourceNotFoundException("경로를 찾을 수 없습니다: " + routeId);
        }
        
        // 2. 경로 일자 정보 조회
        List<RouteDay> days = routeMapper.getRouteDaysByRouteId(routeId);
        
        // 3. 각 일자별 장소 정보 조회
        List<RouteDayDto> dayDtos = new ArrayList<>();
        
        for (RouteDay day : days) {
            List<RoutePlace> places = routeMapper.getRoutePlacesByDayId(day.getDayId());
            
            // 방문 순서대로 정렬
            places.sort(Comparator.comparing(RoutePlace::getVisitOrder));
            
            List<RoutePlaceDto> placeDtos = new ArrayList<>();
            
            for (RoutePlace place : places) {
                // 관광지 정보 조회
                Attraction attraction = attractionService.getAttractionByNo(place.getAttractionNo());
                
                // 이동 정보 조회 (목적지가 현재 장소인 이동 정보)
                RouteTransport transport = routeMapper.getRouteTransportByToPlaceId(place.getPlaceId());
                
                RouteTransportDto transportDto = null;
                if (transport != null) {
                    transportDto = RouteTransportDto.builder()
                            .transportType(transport.getTransportType())
                            .distance(transport.getDistance())
                            .estimatedTime(transport.getEstimatedTime())
                            .description(transport.getDescription())
                            .build();
                }
                
                RoutePlaceDto placeDto = RoutePlaceDto.builder()
                        .placeId(place.getPlaceId())
                        .attraction(AttractionResponseDto.from(attraction))
                        .visitOrder(place.getVisitOrder())
                        .estimatedTime(place.getEstimatedTime())
                        .memo(place.getMemo())
                        .transport(transportDto)
                        .build();
                
                placeDtos.add(placeDto);
            }
            
            RouteDayDto dayDto = RouteDayDto.builder()
                    .dayId(day.getDayId())
                    .dayNumber(day.getDayNumber())
                    .date(day.getDate())
                    .places(placeDtos)
                    .dayTotalTime(day.getDayTotalTime())
                    .build();
            
            dayDtos.add(dayDto);
        }
        
        // 4. 응답 DTO 구성
        return RouteResponseDto.builder()
                .routeId(route.getRouteId())
                .title(route.getTitle())
                .description(route.getDescription())
                .isPublic(route.getIsPublic())
                .startDate(route.getStartDate())
                .endDate(route.getEndDate())
                .viewCount(route.getViewCount())
                .likeCount(route.getLikeCount())
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .days(dayDtos)
                .totalAttractions(countTotalAttractions(dayDtos))
                .totalDays(dayDtos.size())
                .mainTheme(determineMainTheme(dayDtos))
                .build();
    }
    
    /**
     * 총 방문 관광지 수 계산
     */
    private int countTotalAttractions(List<RouteDayDto> days) {
        return days.stream().mapToInt(day -> day.getPlaces().size()).sum();
    }
    
    /**
     * 주요 테마 결정 (가장 많이 방문한 관광지 유형)
     */
    private String determineMainTheme(List<RouteDayDto> days) {
        Map<String, Integer> themeCounts = new HashMap<>();
        
        for (RouteDayDto day : days) {
            for (RoutePlaceDto place : day.getPlaces()) {
                String theme = place.getAttraction().getContentTypeName();
                themeCounts.put(theme, themeCounts.getOrDefault(theme, 0) + 1);
            }
        }
        
        return themeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("일반");
    }
    
    @Override
    @Transactional
    public RouteResponseDto updateRoute(
            Integer routeId, Integer mno, RouteCreateRequestDto requestDto) {
        log.info("경로 수정 요청: routeId={}, mno={}", routeId, mno);
        
        // 1. 기존 경로 조회 및 권한 확인
        TravelRoute route = routeMapper.getRouteById(routeId);
        if (route == null) {
            throw new ResourceNotFoundException("경로를 찾을 수 없습니다: " + routeId);
        }
        
        if (route.getMno() != null && !route.getMno().equals(mno)) {
            throw new UnauthorizedException("이 경로를 수정할 권한이 없습니다");
        }
        
        // 2. 기존 데이터 삭제 (place, transport, day-place mapping)
        routeMapper.deleteRoutePlacesByRouteId(routeId);
        routeMapper.deleteRouteTransportsByRouteId(routeId);
        routeMapper.deleteRouteDaysByRouteId(routeId);
        
        // 3. 경로 기본 정보 업데이트
        route.setTitle(requestDto.getTitle());
        route.setDescription(requestDto.getDescription());
        route.setIsPublic(requestDto.getIsPublic() != null ? requestDto.getIsPublic() : false);
        route.setStartDate(requestDto.getStartDate());
        route.setEndDate(requestDto.getEndDate());
        
        routeMapper.updateRoute(route);
        
        // 4. 새 데이터 생성 (기존 createRoute 로직과 유사)
        // 장소별 날짜 그룹화
        Map<LocalDate, List<RoutePlaceRequestDto>> placesByDate = new HashMap<>();
        
        for (RoutePlaceRequestDto placeRequest : requestDto.getPlaces()) {
            LocalDate date = placeRequest.getVisitDate();
            if (date == null) {
                date = requestDto.getStartDate() != null ? 
                        requestDto.getStartDate() : LocalDate.now();
            }
            
            placesByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(placeRequest);
        }
        
        // 일자 정보 저장 및 장소 할당
        List<LocalDate> sortedDates = new ArrayList<>(placesByDate.keySet());
        Collections.sort(sortedDates);
        
        for (int i = 0; i < sortedDates.size(); i++) {
            LocalDate date = sortedDates.get(i);
            List<RoutePlaceRequestDto> dayPlaces = placesByDate.get(date);
            
            dayPlaces.sort(Comparator.comparing(RoutePlaceRequestDto::getVisitOrder));
            
            RouteDay day = RouteDay.builder()
                    .routeId(routeId)
                    .dayNumber(i + 1)
                    .date(date)
                    .dayTotalTime(calculateDayTotalTime(dayPlaces))
                    .build();
            
            routeMapper.insertRouteDay(day);
            Integer dayId = day.getDayId();
            
            Integer prevPlaceId = null;
            
            for (RoutePlaceRequestDto placeRequest : dayPlaces) {
                Attraction attraction = attractionService.getAttractionByNo(placeRequest.getAttractionNo());
                if (attraction == null) {
                    continue;
                }
                
                RoutePlace place = RoutePlace.builder()
                        .routeId(routeId)
                        .attractionNo(placeRequest.getAttractionNo())
                        .visitOrder(placeRequest.getVisitOrder())
                        .estimatedTime(placeRequest.getEstimatedTime() != null ? 
                                placeRequest.getEstimatedTime() : DEFAULT_VISIT_TIME)
                        .visitDate(date)
                        .memo(placeRequest.getMemo())
                        .build();
                
                routeMapper.insertRoutePlace(place);
                Integer placeId = place.getPlaceId();
                
                routeMapper.insertRouteDayPlace(dayId, placeId);
                
                if (prevPlaceId != null && placeRequest.getTransportType() != null) {
                    RoutePlace prevPlace = routeMapper.getRoutePlaceById(prevPlaceId);
                    Attraction prevAttraction = attractionService.getAttractionByNo(prevPlace.getAttractionNo());
                    
                    Map<String, Integer> travelInfo = calculateTravel(
                            prevAttraction.getLatitude(), prevAttraction.getLongitude(),
                            attraction.getLatitude(), attraction.getLongitude(),
                            placeRequest.getTransportType());
                    
                    RouteTransport transport = RouteTransport.builder()
                            .routeId(routeId)
                            .fromPlaceId(prevPlaceId)
                            .toPlaceId(placeId)
                            .transportType(placeRequest.getTransportType())
                            .distance(travelInfo.get("distance"))
                            .estimatedTime(travelInfo.get("duration") / 60)
                            .description(generateTravelDescription(prevAttraction, attraction, placeRequest.getTransportType()))
                            .build();
                    
                    routeMapper.insertRouteTransport(transport);
                }
                
                prevPlaceId = placeId;
            }
        }
        
        // 5. 결과 조회 및 반환
        return getRouteById(routeId);
    }
    
    @Override
    @Transactional
    public boolean deleteRoute(Integer routeId, Integer mno) {
        log.info("경로 삭제 요청: routeId={}, mno={}", routeId, mno);
        
        // 경로 조회 및 권한 확인
        TravelRoute route = routeMapper.getRouteById(routeId);
        if (route == null) {
            throw new ResourceNotFoundException("경로를 찾을 수 없습니다: " + routeId);
        }
        
        if (route.getMno() != null && !route.getMno().equals(mno)) {
            throw new UnauthorizedException("이 경로를 삭제할 권한이 없습니다");
        }
        
        // 관련 데이터 모두 삭제 (CASCADE로 설정되어 있다면 route만 삭제해도 됨)
        routeMapper.deleteRoutePlacesByRouteId(routeId);
        routeMapper.deleteRouteTransportsByRouteId(routeId);
        routeMapper.deleteRouteDaysByRouteId(routeId);
        
        // 경로 자체 삭제
        return routeMapper.deleteRoute(routeId) > 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RouteResponseDto> getRoutes(Integer mno, boolean onlyMine, int page, int size) {
        log.info("경로 목록 조회 요청: mno={}, onlyMine={}, page={}, size={}", 
                mno, onlyMine, page, size);
        
        int offset = (page - 1) * size;
        
        List<TravelRoute> routes;
        long totalCount;
        
        if (onlyMine) {
            // 내 경로만 조회
            routes = routeMapper.getRoutesByMno(mno, offset, size);
            totalCount = routeMapper.countRoutesByMno(mno);
        } else {
            // 공개 경로 조회 (내 비공개 경로 포함)
            routes = routeMapper.getPublicRoutes(mno, offset, size);
            totalCount = routeMapper.countPublicRoutes(mno);
        }
        
        List<RouteResponseDto> routeDtos = routes.stream()
                .map(route -> {
                    // 기본 정보만 포함된 간략한 DTO 반환
                    List<RouteDay> days = routeMapper.getRouteDaysByRouteId(route.getRouteId());
                    int totalAttractions = routeMapper.countAttractionsByRouteId(route.getRouteId());
                    
                    return RouteResponseDto.builder()
                            .routeId(route.getRouteId())
                            .title(route.getTitle())
                            .description(route.getDescription())
                            .isPublic(route.getIsPublic())
                            .startDate(route.getStartDate())
                            .endDate(route.getEndDate())
                            .viewCount(route.getViewCount())
                            .likeCount(route.getLikeCount())
                            .createdAt(route.getCreatedAt())
                            .updatedAt(route.getUpdatedAt())
                            .totalAttractions(totalAttractions)
                            .totalDays(days.size())
                            .build();
                })
                .collect(Collectors.toList());
        
        // 페이지 객체 생성 및 반환
        return new PageImpl<>(routeDtos, PageRequest.of(page - 1, size), totalCount);
    }
    
    @Override
    @Transactional
    public void incrementViewCount(Integer routeId) {
        routeMapper.incrementViewCount(routeId);
    }
 }