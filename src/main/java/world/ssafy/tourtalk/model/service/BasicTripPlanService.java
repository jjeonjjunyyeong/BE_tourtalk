package world.ssafy.tourtalk.model.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.TripPlan;
import world.ssafy.tourtalk.model.dto.TripPlanAttraction;
import world.ssafy.tourtalk.model.dto.enums.TripPlanStatus;
import world.ssafy.tourtalk.model.dto.request.tripplan.TripPlanCreateRequestDto;
import world.ssafy.tourtalk.model.mapper.TripPlanMapper;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicTripPlanService implements TripPlanService {
    
    private final TripPlanMapper tripPlanMapper;
    
    @Override
    @Transactional
    public TripPlan createTripPlan(Integer mno, TripPlanCreateRequestDto requestDto) {
        // 1. 여행 계획 기본 정보 생성
        TripPlan tripPlan = TripPlan.builder()
                .mno(mno)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .totalDistance(requestDto.getTotalDistance())
                .totalDuration(requestDto.getTotalDuration())
                .status(requestDto.getStatus())
                .build();
        
        int insertedCount = tripPlanMapper.insertTripPlan(tripPlan);
        
        if (insertedCount == 0) {
            throw new RuntimeException("여행 계획 생성에 실패했습니다.");
        }
        
        // 2. 관광지 목록 추가
        if (requestDto.getAttractions() != null && !requestDto.getAttractions().isEmpty()) {
            List<TripPlanAttraction> attractions = IntStream.range(0, requestDto.getAttractions().size())
                    .mapToObj(i -> {
                        TripPlanCreateRequestDto.TripPlanAttractionRequestDto attractionDto = requestDto.getAttractions().get(i);
                        return TripPlanAttraction.builder()
                                .tripPlanId(tripPlan.getId())
                                .attractionNo(attractionDto.getAttractionId())
                                .visitOrder(attractionDto.getVisitOrder())
                                .attractionTitle(attractionDto.getAttractionTitle())
                                .latitude(attractionDto.getLatitude())
                                .longitude(attractionDto.getLongitude())
                                .sido(attractionDto.getSido())
                                .gugun(attractionDto.getGugun())
                                .addr(attractionDto.getAddr())
                                .build();
                    })
                    .toList();
            
            tripPlanMapper.insertTripPlanAttractions(tripPlan.getId(), attractions);
        }
        
        return getTripPlanById(tripPlan.getId(), mno);
    }
    
    @Override
    public TripPlan getTripPlanById(Long id, Integer currentMno) {
        TripPlan tripPlan = tripPlanMapper.getTripPlanById(id);
        if (tripPlan == null) {
            throw new IllegalArgumentException("존재하지 않는 여행 계획입니다: " + id);
        }
        
        // 소유자 확인 (삭제된 계획은 소유자만 조회 가능)
        if (tripPlan.getStatus() == TripPlanStatus.DELETED && !tripPlan.getMno().equals(currentMno)) {
            throw new IllegalArgumentException("삭제된 여행 계획은 조회할 수 없습니다.");
        }
        
        // 관광지 목록 조회
        List<TripPlanAttraction> attractions = tripPlanMapper.getTripPlanAttractionsByTripPlanId(id);
        tripPlan.setAttractions(attractions);
        
        return tripPlan;
    }
    
    @Override
    public List<TripPlan> getUserTripPlans(Integer mno) {
        return tripPlanMapper.getAllTripPlansByMno(mno);
    }
    
    @Override
    public List<TripPlan> getUserTripPlansByStatus(Integer mno, TripPlanStatus status) {
        return tripPlanMapper.getTripPlansByMno(mno, status);
    }
    
    @Override
    @Transactional
    public TripPlan updateTripPlan(Long id, Integer mno, TripPlanCreateRequestDto requestDto) {
        // 1. 기존 여행 계획 조회 및 권한 확인
        TripPlan existingTripPlan = tripPlanMapper.getTripPlanById(id);
        if (existingTripPlan == null) {
            throw new IllegalArgumentException("존재하지 않는 여행 계획입니다: " + id);
        }
        
        if (!existingTripPlan.getMno().equals(mno)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        
        // 2. 여행 계획 기본 정보 수정
        TripPlan updatedTripPlan = TripPlan.builder()
                .id(id)
                .mno(mno)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .totalDistance(requestDto.getTotalDistance())
                .totalDuration(requestDto.getTotalDuration())
                .status(requestDto.getStatus())
                .build();
        
        int updatedCount = tripPlanMapper.updateTripPlan(updatedTripPlan);
        if (updatedCount == 0) {
            throw new RuntimeException("여행 계획 수정에 실패했습니다.");
        }
        
        // 3. 기존 관광지 목록 삭제 후 새로 추가
        tripPlanMapper.deleteTripPlanAttractionsByTripPlanId(id);
        
        if (requestDto.getAttractions() != null && !requestDto.getAttractions().isEmpty()) {
            List<TripPlanAttraction> attractions = IntStream.range(0, requestDto.getAttractions().size())
                    .mapToObj(i -> {
                        TripPlanCreateRequestDto.TripPlanAttractionRequestDto attractionDto = requestDto.getAttractions().get(i);
                        return TripPlanAttraction.builder()
                                .tripPlanId(id)
                                .attractionNo(attractionDto.getAttractionId())
                                .visitOrder(attractionDto.getVisitOrder())
                                .attractionTitle(attractionDto.getAttractionTitle())
                                .latitude(attractionDto.getLatitude())
                                .longitude(attractionDto.getLongitude())
                                .sido(attractionDto.getSido())
                                .gugun(attractionDto.getGugun())
                                .addr(attractionDto.getAddr())
                                .build();
                    })
                    .toList();
            
            tripPlanMapper.insertTripPlanAttractions(id, attractions);
        }
        
        return getTripPlanById(id, mno);
    }
    
    @Override
    @Transactional
    public boolean deleteTripPlan(Long id, Integer mno) {
        TripPlan existingTripPlan = tripPlanMapper.getTripPlanById(id);
        if (existingTripPlan == null) {
            throw new IllegalArgumentException("존재하지 않는 여행 계획입니다: " + id);
        }
        
        if (!existingTripPlan.getMno().equals(mno)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        
        // 관련 관광지 목록도 함께 삭제됨 (CASCADE)
        int deletedCount = tripPlanMapper.deleteTripPlan(id);
        return deletedCount > 0;
    }
    
    @Override
    @Transactional
    public boolean softDeleteTripPlan(Long id, Integer mno) {
        TripPlan existingTripPlan = tripPlanMapper.getTripPlanById(id);
        if (existingTripPlan == null) {
            throw new IllegalArgumentException("존재하지 않는 여행 계획입니다: " + id);
        }
        
        if (!existingTripPlan.getMno().equals(mno)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        
        int updatedCount = tripPlanMapper.softDeleteTripPlan(id);
        return updatedCount > 0;
    }
    
    @Override
    public Page<TripPlan> getUserTripPlansWithPaging(Integer mno, TripPlanStatus status, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        
        List<TripPlan> tripPlans = tripPlanMapper.getTripPlansWithPaging(mno, status, offset, pageSize);
        
        // 각 여행 계획에 대한 관광지 목록 조회
        for (TripPlan tripPlan : tripPlans) {
            List<TripPlanAttraction> attractions = tripPlanMapper.getTripPlanAttractionsByTripPlanId(tripPlan.getId());
            tripPlan.setAttractions(attractions);
        }
        
        int totalCount = tripPlanMapper.countTripPlansByMno(mno);
        
        return createPage(tripPlans, pageNumber, pageSize, totalCount);
    }
    
    @Override
    public boolean isOwner(Long tripPlanId, Integer mno) {
        return tripPlanMapper.isOwner(tripPlanId, mno);
    }
    
    @Override
    public boolean existsTripPlan(Long id) {
        return tripPlanMapper.existsTripPlanById(id);
    }
    
    private Page<TripPlan> createPage(List<TripPlan> content, int pageNumber, int pageSize, long totalCount) {
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        Page<TripPlan> page = Page.<TripPlan>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(totalCount)
                .totalPages(totalPages)
                .build();
        
        page.calculatePageInfo(10);
        
        return page;
    }
}