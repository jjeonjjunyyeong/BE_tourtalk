package world.ssafy.tourtalk.restcontroller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.controller.RestControllerHelper;
import world.ssafy.tourtalk.exception.ResourceNotFoundException;
import world.ssafy.tourtalk.exception.UnauthorizedException;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.request.route.RouteCreateRequestDto;
import world.ssafy.tourtalk.model.dto.request.route.RouteRecommendRequestDto;
import world.ssafy.tourtalk.model.dto.response.common.PageResponseDto;
import world.ssafy.tourtalk.model.dto.response.route.RouteResponseDto;
import world.ssafy.tourtalk.model.service.RouteService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;

@Slf4j
@RestController
@RequestMapping("/api/v1/routes")
@Tag(name = "Routes", description = "여행 경로 추천 및 관리 API")
@RequiredArgsConstructor
public class RouteRestController implements RestControllerHelper {
    
    private final RouteService routeService;
    
    @Operation(
        summary = "경로 추천",
        description = "선택한 관광지 목록을 기반으로 최적 여행 경로를 추천합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "경로 추천 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
        }
    )
    @PostMapping("/recommend")
    public ResponseEntity<?> recommendRoute(
            @AuthenticationPrincipal CustomMemberPrincipal principal,
            @Valid @RequestBody RouteRecommendRequestDto requestDto) {
        try {
            Integer mno = principal != null ? principal.getMno() : null;
            log.info("경로 추천 요청: mno={}, attractions={}", mno, requestDto.getAttractionIds());
            
            RouteResponseDto responseDto = routeService.recommendRoute(mno, requestDto);
            
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException e) {
            log.warn("경로 추천 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("경로 추천 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "경로 추천 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(
        summary = "경로 상세 조회",
        description = "경로 ID로 여행 경로 상세 정보를 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "경로 조회 성공"),
            @ApiResponse(responseCode = "404", description = "경로를 찾을 수 없음")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getRouteById(@PathVariable("id") Integer routeId) {
        try {
            RouteResponseDto responseDto = routeService.getRouteById(routeId);
            
            // 조회수 증가
            routeService.incrementViewCount(routeId);
            
            return ResponseEntity.ok(responseDto);
        } catch (ResourceNotFoundException e) {
            log.warn("경로 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("경로 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "경로 조회 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(
        summary = "경로 생성",
        description = "새로운 여행 경로를 생성합니다.",
        security = @SecurityRequirement(name = "JWT"),
        responses = {
            @ApiResponse(responseCode = "201", description = "경로 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
        }
    )
    @PostMapping
    public ResponseEntity<?> createRoute(
            @AuthenticationPrincipal CustomMemberPrincipal principal,
            @Valid @RequestBody RouteCreateRequestDto requestDto) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized", "message", "로그인이 필요합니다."));
            }
            
            RouteResponseDto responseDto = routeService.createRoute(principal.getMno(), requestDto);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException e) {
            log.warn("경로 생성 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("경로 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "경로 생성 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(
        summary = "경로 수정",
        description = "기존 여행 경로를 수정합니다.",
        security = @SecurityRequirement(name = "JWT"),
        responses = {
            @ApiResponse(responseCode = "200", description = "경로 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "경로를 찾을 수 없음")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoute(
            @AuthenticationPrincipal CustomMemberPrincipal principal,
            @PathVariable("id") Integer routeId,
            @Valid @RequestBody RouteCreateRequestDto requestDto) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized", "message", "로그인이 필요합니다."));
            }
            
            RouteResponseDto responseDto = routeService.updateRoute(routeId, principal.getMno(), requestDto);
            
            return ResponseEntity.ok(responseDto);
        } catch (ResourceNotFoundException e) {
            log.warn("경로 수정 실패 - 존재하지 않는 경로: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (UnauthorizedException e) {
            log.warn("경로 수정 실패 - 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("경로 수정 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("경로 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "경로 수정 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(
        summary = "경로 삭제",
        description = "여행 경로를 삭제합니다.",
        security = @SecurityRequirement(name = "JWT"),
        responses = {
            @ApiResponse(responseCode = "204", description = "경로 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "경로를 찾을 수 없음")
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(
            @AuthenticationPrincipal CustomMemberPrincipal principal,
            @PathVariable("id") Integer routeId) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized", "message", "로그인이 필요합니다."));
            }
            
            boolean deleted = routeService.deleteRoute(routeId, principal.getMno());
            
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Internal Server Error", "message", "경로 삭제에 실패했습니다."));
            }
        } catch (ResourceNotFoundException e) {
            log.warn("경로 삭제 실패 - 존재하지 않는 경로: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (UnauthorizedException e) {
            log.warn("경로 삭제 실패 - 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("경로 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "경로 삭제 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(
        summary = "경로 목록 조회",
        description = "여행 경로 목록을 조회합니다. 로그인한 경우 자신의 경로만 보거나 공개 경로를 볼 수 있습니다.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "경로 목록 조회 성공",
                content = @Content(schema = @Schema(implementation = PageResponseDto.class))
            )
        }
    )
    @GetMapping
    public ResponseEntity<?> getRoutes(
            @AuthenticationPrincipal CustomMemberPrincipal principal,
            @RequestParam(required = false, defaultValue = "false") boolean mine,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        try {
            Integer mno = principal != null ? principal.getMno() : null;
            
            Page<RouteResponseDto> result = routeService.getRoutes(mno, mine, page, size);
            
            PageResponseDto<RouteResponseDto> response = PageResponseDto.<RouteResponseDto>builder()
                    .content(result.getContent())
                    .pageNumber(result.getNumber() + 1)
                    .pageSize(result.getSize())
                    .totalPages(result.getTotalPages())
                    .totalElements(result.getTotalElements())
                    .first(result.isFirst())
                    .last(result.isLast())
                    .build();
            
            // 페이지 내비게이션 정보 계산
            int navSize = 5;
            int startPage = ((response.getPageNumber() - 1) / navSize) * navSize + 1;
            int tempEndPage = startPage + navSize - 1;
            int endPage = Math.min(tempEndPage, response.getTotalPages());
            
            response.setStartPage(startPage);
            response.setEndPage(endPage);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("경로 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "경로 목록 조회 중 오류가 발생했습니다."));
        }
    }
}