package world.ssafy.tourtalk.model.service;

import java.util.List;

import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.request.route.RouteCreateRequestDto;
import world.ssafy.tourtalk.model.dto.request.route.RouteRecommendRequestDto;
import world.ssafy.tourtalk.model.dto.response.route.RouteResponseDto;


public interface RouteService {
    // 사용자 정의 경로 관리
    RouteResponseDto createRoute(Integer mno, RouteCreateRequestDto requestDto);
    RouteResponseDto getRouteById(Integer routeId);
    Page<RouteResponseDto> getUserRoutes(Integer mno, int page, int size);
    Page<RouteResponseDto> getPublicRoutes(int page, int size);
    RouteResponseDto updateRoute(Integer routeId, Integer mno, RouteCreateRequestDto requestDto);
    boolean deleteRoute(Integer routeId, Integer mno);
    
    // 경로 추천 기능
    RouteResponseDto recommendRoute(Integer mno, RouteRecommendRequestDto requestDto);
    List<RouteResponseDto> getPopularRoutes(int limit);
    List<RouteResponseDto> getThemeBasedRoutes(String theme, int limit);
    List<RouteResponseDto> getRegionBasedRoutes(Integer sidoCode, int limit);
    
    // 기타 기능
    void incrementViewCount(Integer routeId);
    boolean toggleLike(Integer routeId, Integer mno);
}
