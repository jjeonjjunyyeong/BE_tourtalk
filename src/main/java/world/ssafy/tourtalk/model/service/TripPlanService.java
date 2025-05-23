package world.ssafy.tourtalk.model.service;

import java.util.List;

import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.TripPlan;
import world.ssafy.tourtalk.model.dto.enums.TripPlanStatus;
import world.ssafy.tourtalk.model.dto.request.tripplan.TripPlanCreateRequestDto;

public interface TripPlanService {
    
    // 여행 계획 CRUD
    TripPlan createTripPlan(Integer mno, TripPlanCreateRequestDto requestDto);
    TripPlan getTripPlanById(Long id, Integer currentMno);
    List<TripPlan> getUserTripPlans(Integer mno);
    List<TripPlan> getUserTripPlansByStatus(Integer mno, TripPlanStatus status);
    TripPlan updateTripPlan(Long id, Integer mno, TripPlanCreateRequestDto requestDto);
    boolean deleteTripPlan(Long id, Integer mno);
    boolean softDeleteTripPlan(Long id, Integer mno);
    
    // 페이징
    Page<TripPlan> getUserTripPlansWithPaging(Integer mno, TripPlanStatus status, int pageNumber, int pageSize);
    
    // 유효성 검증
    boolean isOwner(Long tripPlanId, Integer mno);
    boolean existsTripPlan(Long id);
}