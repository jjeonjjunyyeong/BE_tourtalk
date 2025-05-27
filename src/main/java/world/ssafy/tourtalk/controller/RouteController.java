package world.ssafy.tourtalk.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
import world.ssafy.tourtalk.model.dto.request.route.RouteRequestDto;
import world.ssafy.tourtalk.model.dto.response.common.Coordinate;
import world.ssafy.tourtalk.model.dto.response.route.RouteResponseDto;
import world.ssafy.tourtalk.model.service.RouteService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routes")
@Tag(name = "Routes", description = "경로 검색 API")
public class RouteController {
    
    private final RouteService routeService;
    
    @Operation(summary = "경로 검색", description = "출발지, 목적지, 경유지를 통한 최적 경로를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "경로 검색 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "500", description = "경로 검색 실패")
    @PostMapping("/search")
    public ResponseEntity<?> searchRoute(@Valid @RequestBody RouteRequestDto requestDto) {
        try {
            log.info("경로 검색 요청 - 출발: [{}, {}], 목적: [{}, {}], 경유지: {}", 
                requestDto.getOrigin().getX(), requestDto.getOrigin().getY(),
                requestDto.getDestination().getX(), requestDto.getDestination().getY(),
                requestDto.getWaypoints() != null ? requestDto.getWaypoints().size() : 0);
            
            RouteResponseDto routeResponse = routeService.getRoute(requestDto);
            
            log.info("경로 검색 성공 - 거리: {}m, 시간: {}초", 
                routeResponse.getRouteInfo().getTotalDistance(),
                routeResponse.getRouteInfo().getTotalTime());
            
            return ResponseEntity.ok(routeResponse);
            
        } catch (IllegalArgumentException e) {
            log.warn("경로 검색 요청 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("경로 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("경로를 찾을 수 없습니다. 잠시 후 다시 시도해주세요.");
        }
    }
    
    @Operation(summary = "간단 경로 검색", description = "출발지와 목적지만으로 경로를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "경로 검색 성공")
    @GetMapping("/simple")
    public ResponseEntity<?> searchSimpleRoute(
            @RequestParam("originX") Double originX,
            @RequestParam("originY") Double originY,
            @RequestParam("destX") Double destX,
            @RequestParam("destY") Double destY,
            @RequestParam(value = "originName", required = false) String originName,
            @RequestParam(value = "destName", required = false) String destName) {
        
        try {
            RouteRequestDto requestDto = RouteRequestDto.builder()
                .origin(Coordinate.builder()
                    .longitude(BigDecimal.valueOf(originX))
                    .latitude(BigDecimal.valueOf(originY))
                    .name(originName)
                    .build())
                .destination(Coordinate.builder()
                    .longitude(BigDecimal.valueOf(destX))
                    .latitude(BigDecimal.valueOf(destY))
                    .name(destName)
                    .build())
                .build();
            
            RouteResponseDto routeResponse = routeService.getRoute(requestDto);
            return ResponseEntity.ok(routeResponse);
            
        } catch (Exception e) {
            log.error("간단 경로 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("경로를 찾을 수 없습니다.");
        }
    }
    
    @Operation(summary = "다중 경로 검색", description = "여러 지점을 순서대로 방문하는 경로를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "경로 검색 성공")
    @PostMapping("/multi-point")
    public ResponseEntity<?> searchMultiPointRoute(@RequestBody List<Coordinate> points) {
        try {
            if (points == null || points.size() < 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("최소 2개 이상의 지점이 필요합니다.");
            }
            
            if (points.size() > 5) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("최대 5개 지점까지만 설정할 수 있습니다.");
            }
            
            // 첫 번째와 마지막을 출발지/목적지로, 중간을 경유지로 설정
            List<Coordinate> waypoints = points.size() > 2 ? 
                points.subList(1, points.size() - 1) : null;
            
            RouteRequestDto requestDto = RouteRequestDto.builder()
                .origin(points.get(0))
                .destination(points.get(points.size() - 1))
                .waypoints(waypoints)
                .build();
            
            log.info("다중 지점 경로 검색 - 총 {}개 지점", points.size());
            
            RouteResponseDto routeResponse = routeService.getRoute(requestDto);
            return ResponseEntity.ok(routeResponse);
            
        } catch (Exception e) {
            log.error("다중 지점 경로 검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("경로를 찾을 수 없습니다.");
        }
    }
    
    @Operation(summary = "경로 검색 옵션", description = "경로 검색에 사용할 수 있는 옵션들을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "옵션 조회 성공")
    @GetMapping("/options")
    public ResponseEntity<?> getRouteOptions() {
        try {
            var options = java.util.Map.of(
                "priority", java.util.List.of(
                    java.util.Map.of("value", "RECOMMEND", "label", "추천 경로"),
                    java.util.Map.of("value", "TIME", "label", "빠른 경로"),
                    java.util.Map.of("value", "DISTANCE", "label", "짧은 경로")
                ),
                "carFuel", java.util.List.of(
                    java.util.Map.of("value", "GASOLINE", "label", "휘발유"),
                    java.util.Map.of("value", "DIESEL", "label", "경유"),
                    java.util.Map.of("value", "LPG", "label", "LPG")
                ),
                "limits", java.util.Map.of(
                    "maxWaypoints", 3,
                    "maxTotalPoints", 5,
                    "maxDistance", 500 // km
                )
            );
            
            return ResponseEntity.ok(options);
            
        } catch (Exception e) {
            log.error("경로 옵션 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("옵션 정보를 불러올 수 없습니다.");
        }
    }
    
    @Operation(summary = "좌표 유효성 검증", description = "좌표가 유효한지 검증합니다.")
    @ApiResponse(responseCode = "200", description = "검증 완료")
    @PostMapping("/validate")
    public ResponseEntity<?> validateCoordinates(@RequestBody List<Coordinate> coordinates) {
        try {
            var validationResults = coordinates.stream()
                .map(coord -> java.util.Map.of(
                    "coordinate", coord,
                    "valid", coord.isValid(),
                    "message", coord.isValid() ? "유효한 좌표" : "유효하지 않은 좌표"
                ))
                .collect(Collectors.toList());
            
            boolean allValid = coordinates.stream().allMatch(Coordinate::isValid);
            
            var response = java.util.Map.of(
                "allValid", allValid,
                "results", validationResults
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("좌표 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("좌표 검증 중 오류가 발생했습니다.");
        }
    }
}