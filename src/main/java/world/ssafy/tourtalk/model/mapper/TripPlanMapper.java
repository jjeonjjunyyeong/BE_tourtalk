package world.ssafy.tourtalk.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import world.ssafy.tourtalk.model.dto.TripPlan;
import world.ssafy.tourtalk.model.dto.TripPlanAttraction;
import world.ssafy.tourtalk.model.dto.enums.TripPlanStatus;

@Mapper
public interface TripPlanMapper {
    
    // 여행 계획 CRUD
    int insertTripPlan(TripPlan tripPlan);
    TripPlan getTripPlanById(@Param("id") Long id);
    List<TripPlan> getTripPlansByMno(@Param("mno") Integer mno, @Param("status") TripPlanStatus status);
    List<TripPlan> getAllTripPlansByMno(@Param("mno") Integer mno);
    int updateTripPlan(TripPlan tripPlan);
    int deleteTripPlan(@Param("id") Long id);
    int softDeleteTripPlan(@Param("id") Long id);
    
    // 여행 계획 관광지 CRUD
    int insertTripPlanAttraction(TripPlanAttraction attraction);
    int insertTripPlanAttractions(@Param("tripPlanId") Long tripPlanId, 
                                  @Param("attractions") List<TripPlanAttraction> attractions);
    List<TripPlanAttraction> getTripPlanAttractionsByTripPlanId(@Param("tripPlanId") Long tripPlanId);
    int deleteTripPlanAttractionsByTripPlanId(@Param("tripPlanId") Long tripPlanId);
    
    // 통계 및 검증
    int countTripPlansByMno(@Param("mno") Integer mno);
    boolean existsTripPlanById(@Param("id") Long id);
    boolean isOwner(@Param("tripPlanId") Long tripPlanId, @Param("mno") Integer mno);
    
    // 페이징
    List<TripPlan> getTripPlansWithPaging(@Param("mno") Integer mno, 
                                          @Param("status") TripPlanStatus status,
                                          @Param("offset") int offset, 
                                          @Param("limit") int limit);
}