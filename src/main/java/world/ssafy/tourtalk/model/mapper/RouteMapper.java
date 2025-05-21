package world.ssafy.tourtalk.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.RouteDay;
import world.ssafy.tourtalk.model.dto.RoutePlace;
import world.ssafy.tourtalk.model.dto.RouteTransport;


@Mapper
public interface RouteMapper {
    // 경로 관련
    void insertRoute(TravelRoute route);
    TravelRoute getRouteById(Integer routeId);
    List<TravelRoute> getRoutesByMno(@Param("mno") Integer mno, @Param("offset") int offset, @Param("limit") int limit);
    List<TravelRoute> getPublicRoutes(@Param("mno") Integer mno, @Param("offset") int offset, @Param("limit") int limit);
    long countRoutesByMno(Integer mno);
    long countPublicRoutes(Integer mno);
    void updateRoute(TravelRoute route);
    int deleteRoute(Integer routeId);
    void incrementViewCount(Integer routeId);
    int countAttractionsByRouteId(Integer routeId);
    
    // 일자 관련
    void insertRouteDay(RouteDay day);
    List<RouteDay> getRouteDaysByRouteId(Integer routeId);
    void deleteRouteDaysByRouteId(Integer routeId);
    
    // 장소 관련
    void insertRoutePlace(RoutePlace place);
    RoutePlace getRoutePlaceById(Integer placeId);
    List<RoutePlace> getRoutePlacesByRouteId(Integer routeId);
    List<RoutePlace> getRoutePlacesByDayId(Integer dayId);
    void deleteRoutePlacesByRouteId(Integer routeId);
    
    // 일자-장소 매핑 관련
    void insertRouteDayPlace(@Param("dayId") Integer dayId, @Param("placeId") Integer placeId);
    void deleteRouteDayPlacesByDayId(Integer dayId);
    
    // 이동 관련
    void insertRouteTransport(RouteTransport transport);
    RouteTransport getRouteTransportByToPlaceId(Integer toPlaceId);
    void deleteRouteTransportsByRouteId(Integer routeId);
}