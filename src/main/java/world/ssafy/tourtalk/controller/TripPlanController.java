package world.ssafy.tourtalk.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.TripPlan;
import world.ssafy.tourtalk.model.dto.enums.TripPlanStatus;
import world.ssafy.tourtalk.model.dto.request.tripplan.TripPlanCreateRequestDto;
import world.ssafy.tourtalk.model.dto.response.TripPlanResponseDto;
import world.ssafy.tourtalk.model.dto.response.common.PageResponseDto;
import world.ssafy.tourtalk.model.service.TripPlanService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip-plans")
@Tag(name="Trip Plans", description="여행 계획 관리 API")
public class TripPlanController {

    private final TripPlanService tripPlanService;

    @Operation(summary="여행 계획 생성", description="새로운 여행 계획을 생성합니다.")
    @ApiResponse(responseCode="201", description="여행 계획 생성 성공")
    @ApiResponse(responseCode="400", description="잘못된 요청 데이터")
    @ApiResponse(responseCode="401", description="인증 실패")
    @PostMapping
    public ResponseEntity<?> createTripPlan(
            @AuthenticationPrincipal CustomMemberPrincipal principal,
            @Valid @RequestBody TripPlanCreateRequestDto requestDto) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            log.info("여행 계획 생성 요청 - 사용자: {}, 계획명: {}", principal.getMno(), requestDto.getName());

            TripPlan createdTripPlan = tripPlanService.createTripPlan(principal.getMno(), requestDto);
            TripPlanResponseDto responseDto = TripPlanResponseDto.from(createdTripPlan);

            log.info("여행 계획 생성 완료 - ID: {}, 계획명: {}", createdTripPlan.getId(), createdTripPlan.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (IllegalArgumentException e) {
            log.warn("여행 계획 생성 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("여행 계획 생성 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("여행 계획 생성 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary="여행 계획 상세 조회", description="여행 계획 상세 정보를 조회합니다.")
    @ApiResponse(responseCode="200", description="여행 계획 조회 성공")
    @ApiResponse(responseCode="404", description="여행 계획을 찾을 수 없음")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTripPlan(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomMemberPrincipal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            TripPlan tripPlan = tripPlanService.getTripPlanById(id, principal.getMno());
            TripPlanResponseDto responseDto = TripPlanResponseDto.from(tripPlan);

            return ResponseEntity.ok(responseDto);

        } catch (IllegalArgumentException e) {
            log.warn("여행 계획 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("여행 계획 조회 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("여행 계획 조회 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary="내 여행 계획 목록 조회", description="현재 사용자의 여행 계획 목록을 조회합니다.")
    @ApiResponse(responseCode="200", description="여행 계획 목록 조회 성공")
    @GetMapping
    public ResponseEntity<?> getUserTripPlans(
            @AuthenticationPrincipal CustomMemberPrincipal principal,
            @RequestParam(required = false) TripPlanStatus status,
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            if (page <= 0 || size <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("페이지 번호와 크기는 1 이상이어야 합니다.");
            }

            Page<TripPlan> pageResult = tripPlanService.getUserTripPlansWithPaging(
                    principal.getMno(), status, page, size);

            List<TripPlanResponseDto> dtoContent = pageResult.getContent().stream()
                    .map(TripPlanResponseDto::from)
                    .collect(Collectors.toList());

            PageResponseDto<TripPlanResponseDto> pageResponse = PageResponseDto.<TripPlanResponseDto>builder()
                    .content(dtoContent)
                    .pageNumber(pageResult.getPageNumber())
                    .pageSize(pageResult.getPageSize())
                    .totalPages(pageResult.getTotalPages())
                    .totalElements(pageResult.getTotalElements())
                    .first(pageResult.isFirst())
                    .last(pageResult.isLast())
                    .startPage(pageResult.getStartPage())
                    .endPage(pageResult.getEndPage())
                    .build();

            return ResponseEntity.ok(pageResponse);

        } catch (Exception e) {
            log.error("여행 계획 목록 조회 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("여행 계획 목록 조회 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary="여행 계획 수정", description="기존 여행 계획 정보를 수정합니다.")
    @ApiResponse(responseCode="200", description="여행 계획 수정 성공")
    @ApiResponse(responseCode="400", description="잘못된 요청 데이터")
    @ApiResponse(responseCode="403", description="수정 권한 없음")
    @ApiResponse(responseCode="404", description="여행 계획을 찾을 수 없음")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTripPlan(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomMemberPrincipal principal,
            @Valid @RequestBody TripPlanCreateRequestDto requestDto) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            log.info("여행 계획 수정 요청 - ID: {}, 사용자: {}", id, principal.getMno());

            TripPlan updatedTripPlan = tripPlanService.updateTripPlan(id, principal.getMno(), requestDto);
            TripPlanResponseDto responseDto = TripPlanResponseDto.from(updatedTripPlan);

            log.info("여행 계획 수정 완료 - ID: {}", id);
            return ResponseEntity.ok(responseDto);

        } catch (IllegalArgumentException e) {
            log.warn("여행 계획 수정 실패: {}", e.getMessage());

            if (e.getMessage().contains("권한")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else if (e.getMessage().contains("존재하지 않는")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } catch (Exception e) {
            log.error("여행 계획 수정 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("여행 계획 수정 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary="여행 계획 삭제", description="여행 계획을 완전히 삭제합니다.")
    @ApiResponse(responseCode="204", description="여행 계획 삭제 성공")
    @ApiResponse(responseCode="403", description="삭제 권한 없음")
    @ApiResponse(responseCode="404", description="여행 계획을 찾을 수 없음")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTripPlan(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomMemberPrincipal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            log.info("여행 계획 삭제 요청 - ID: {}, 사용자: {}", id, principal.getMno());

            boolean deleted = tripPlanService.deleteTripPlan(id, principal.getMno());
            if (deleted) {
                log.info("여행 계획 삭제 완료 - ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("여행 계획 삭제에 실패했습니다.");
            }

        } catch (IllegalArgumentException e) {
            log.warn("여행 계획 삭제 실패: {}", e.getMessage());

            if (e.getMessage().contains("권한")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        } catch (Exception e) {
            log.error("여행 계획 삭제 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("여행 계획 삭제 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary="여행 계획 소프트 삭제", description="여행 계획을 삭제 상태로 변경합니다.")
    @ApiResponse(responseCode="200", description="여행 계획 소프트 삭제 성공")
    @ApiResponse(responseCode="403", description="삭제 권한 없음")
    @ApiResponse(responseCode="404", description="여행 계획을 찾을 수 없음")
    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<?> softDeleteTripPlan(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomMemberPrincipal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            log.info("여행 계획 소프트 삭제 요청 - ID: {}, 사용자: {}", id, principal.getMno());

            boolean deleted = tripPlanService.softDeleteTripPlan(id, principal.getMno());
            if (deleted) {
                log.info("여행 계획 소프트 삭제 완료 - ID: {}", id);
                return ResponseEntity.ok("여행 계획이 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("여행 계획 삭제에 실패했습니다.");
            }

        } catch (IllegalArgumentException e) {
            log.warn("여행 계획 소프트 삭제 실패: {}", e.getMessage());

            if (e.getMessage().contains("권한")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        } catch (Exception e) {
            log.error("여행 계획 소프트 삭제 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("여행 계획 삭제 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary="여행 계획 상태 업데이트", description="여행 계획의 상태를 변경합니다.")
    @ApiResponse(responseCode="200", description="상태 변경 성공")
    @ApiResponse(responseCode="403", description="수정 권한 없음")
    @ApiResponse(responseCode="404", description="여행 계획을 찾을 수 없음")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTripPlanStatus(
            @PathVariable Long id,
            @RequestParam TripPlanStatus status,
            @AuthenticationPrincipal CustomMemberPrincipal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            log.info("여행 계획 상태 변경 요청 - ID: {}, 상태: {}, 사용자: {}", id, status, principal.getMno());

            // 기존 여행 계획 조회
            TripPlan existingTripPlan = tripPlanService.getTripPlanById(id, principal.getMno());
            
            // 상태만 변경한 요청 DTO 생성
            TripPlanCreateRequestDto updateDto = TripPlanCreateRequestDto.builder()
                    .name(existingTripPlan.getName())
                    .description(existingTripPlan.getDescription())
                    .startDate(existingTripPlan.getStartDate())
                    .endDate(existingTripPlan.getEndDate())
                    .totalDistance(existingTripPlan.getTotalDistance())
                    .totalDuration(existingTripPlan.getTotalDuration())
                    .status(status)
                    .attractions(existingTripPlan.getAttractions().stream()
                            .map(attr -> TripPlanCreateRequestDto.TripPlanAttractionRequestDto.builder()
                                    .attractionId(attr.getAttractionNo())
                                    .visitOrder(attr.getVisitOrder())
                                    .attractionTitle(attr.getAttractionTitle())
                                    .latitude(attr.getLatitude())
                                    .longitude(attr.getLongitude())
                                    .sido(attr.getSido())
                                    .gugun(attr.getGugun())
                                    .addr(attr.getAddr())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            TripPlan updatedTripPlan = tripPlanService.updateTripPlan(id, principal.getMno(), updateDto);
            TripPlanResponseDto responseDto = TripPlanResponseDto.from(updatedTripPlan);

            log.info("여행 계획 상태 변경 완료 - ID: {}, 새 상태: {}", id, status);
            return ResponseEntity.ok(responseDto);

        } catch (IllegalArgumentException e) {
            log.warn("여행 계획 상태 변경 실패: {}", e.getMessage());

            if (e.getMessage().contains("권한")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        } catch (Exception e) {
            log.error("여행 계획 상태 변경 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("여행 계획 상태 변경 중 오류가 발생했습니다.");
        }
    }
}