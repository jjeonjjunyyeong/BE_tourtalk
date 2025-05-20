package world.ssafy.tourtalk.restcontroller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import world.ssafy.tourtalk.config.TemporaryUserService;
import world.ssafy.tourtalk.controller.RestControllerHelper;
import world.ssafy.tourtalk.model.dto.Hotplace;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.request.hotplace.HotplaceCreateRequestDto;
import world.ssafy.tourtalk.model.dto.response.hotplace.HotplaceResponseDto;
import world.ssafy.tourtalk.model.dto.response.common.PageResponseDto;
import world.ssafy.tourtalk.model.service.HotplaceService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hotplace 관리 REST 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/hotplaces")
@Tag(name="Hotplaces", description="사용자 Hotplace 관리 API")
@RequiredArgsConstructor
public class HotplaceRestController implements RestControllerHelper {
    
    private final HotplaceService hotplaceService;
    private final TemporaryUserService temporaryUserService;
    
    @Operation(summary="Hotplace 등록", description="새로운 Hotplace를 등록합니다.")
    @ApiResponse(responseCode="201", description="Hotplace 등록 성공")
    @ApiResponse(responseCode="400", description="잘못된 요청 데이터")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createHotplace(
            @Valid @ModelAttribute HotplaceCreateRequestDto requestDto,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String userId = temporaryUserService.getCurrentUserId(request, response);
            
            log.info("Hotplace 등록 요청 - 사용자: {}, 제목: {}", userId, requestDto.getTitle());
            
            Hotplace createdHotplace = hotplaceService.createHotplace(userId, requestDto);
            HotplaceResponseDto responseDto = HotplaceResponseDto.from(createdHotplace);
            
            log.info("Hotplace 등록 완료 - ID: {}, 제목: {}", createdHotplace.getId(), createdHotplace.getTitle());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
            
        } catch (IllegalArgumentException e) {
            log.warn("Hotplace 등록 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("BAD_REQUEST", e.getMessage()));
        } catch (Exception e) {
            log.error("Hotplace 등록 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "Hotplace 등록 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(summary="Hotplace 상세 조회", description="Hotplace 상세 정보를 조회합니다.")
    @ApiResponse(responseCode="200", description="Hotplace 조회 성공")
    @ApiResponse(responseCode="404", description="Hotplace를 찾을 수 없음")
    @GetMapping("/{id}")
    public ResponseEntity<?> getHotplace(
            @PathVariable Long id,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String userId = temporaryUserService.getCurrentUserId(request, response);
            
            Hotplace hotplace = hotplaceService.getHotplaceById(id, userId);
            HotplaceResponseDto responseDto = HotplaceResponseDto.from(hotplace);
            
            return ResponseEntity.ok(responseDto);
            
        } catch (IllegalArgumentException e) {
            log.warn("Hotplace 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            log.error("Hotplace 조회 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "Hotplace 조회 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(summary="Hotplace 목록 조회", description="전체 Hotplace 목록을 조회합니다.")
    @ApiResponse(responseCode="200", description="Hotplace 목록 조회 성공")
    @GetMapping
    public ResponseEntity<?> getAllHotplaces(
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String userId = temporaryUserService.getCurrentUserId(request, response);
            
            Page<Hotplace> pageResult = hotplaceService.getAllHotplaces(page, size, userId);
            
            List<HotplaceResponseDto> dtoContent = pageResult.getContent().stream()
                    .map(HotplaceResponseDto::from)
                    .collect(Collectors.toList());
            
            PageResponseDto<HotplaceResponseDto> pageResponse = PageResponseDto.<HotplaceResponseDto>builder()
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
            log.error("Hotplace 목록 조회 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "Hotplace 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(summary="내 Hotplace 목록 조회", description="현재 사용자가 등록한 Hotplace 목록을 조회합니다.")
    @ApiResponse(responseCode="200", description="내 Hotplace 목록 조회 성공")
    @GetMapping("/my")
    public ResponseEntity<?> getMyHotplaces(
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String userId = temporaryUserService.getCurrentUserId(request, response);
            
            Page<Hotplace> pageResult = hotplaceService.getMyHotplaces(userId, page, size);
            
            List<HotplaceResponseDto> dtoContent = pageResult.getContent().stream()
                    .map(HotplaceResponseDto::from)
                    .collect(Collectors.toList());
            
            PageResponseDto<HotplaceResponseDto> pageResponse = PageResponseDto.<HotplaceResponseDto>builder()
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
            log.error("내 Hotplace 목록 조회 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "내 Hotplace 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(summary="Hotplace 수정", description="기존 Hotplace 정보를 수정합니다.")
    @ApiResponse(responseCode="200", description="Hotplace 수정 성공")
    @ApiResponse(responseCode="400", description="잘못된 요청 데이터")
    @ApiResponse(responseCode="403", description="수정 권한 없음")
    @ApiResponse(responseCode="404", description="Hotplace를 찾을 수 없음")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateHotplace(
            @PathVariable Long id,
            @Valid @ModelAttribute HotplaceCreateRequestDto requestDto,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String userId = temporaryUserService.getCurrentUserId(request, response);
            
            log.info("Hotplace 수정 요청 - ID: {}, 사용자: {}", id, userId);
            
            Hotplace updatedHotplace = hotplaceService.updateHotplace(id, userId, requestDto);
            HotplaceResponseDto responseDto = HotplaceResponseDto.from(updatedHotplace);
            
            log.info("Hotplace 수정 완료 - ID: {}", id);
            return ResponseEntity.ok(responseDto);
            
        } catch (IllegalArgumentException e) {
            log.warn("Hotplace 수정 실패: {}", e.getMessage());
            
            if (e.getMessage().contains("권한")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("FORBIDDEN", e.getMessage()));
            } else if (e.getMessage().contains("존재하지 않는")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("NOT_FOUND", e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("BAD_REQUEST", e.getMessage()));
            }
        } catch (Exception e) {
            log.error("Hotplace 수정 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "Hotplace 수정 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(summary="Hotplace 삭제", description="Hotplace를 삭제합니다.")
    @ApiResponse(responseCode="204", description="Hotplace 삭제 성공")
    @ApiResponse(responseCode="403", description="삭제 권한 없음")
    @ApiResponse(responseCode="404", description="Hotplace를 찾을 수 없음")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHotplace(
            @PathVariable Long id,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String userId = temporaryUserService.getCurrentUserId(request, response);
            
            log.info("Hotplace 삭제 요청 - ID: {}, 사용자: {}", id, userId);
            
            boolean deleted = hotplaceService.deleteHotplace(id, userId);
            if (deleted) {
                log.info("Hotplace 삭제 완료 - ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("DELETE_FAILED", "Hotplace 삭제에 실패했습니다."));
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("Hotplace 삭제 실패: {}", e.getMessage());
            
            if (e.getMessage().contains("권한")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("FORBIDDEN", e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("NOT_FOUND", e.getMessage()));
            }
        } catch (Exception e) {
            log.error("Hotplace 삭제 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "Hotplace 삭제 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(summary="Hotplace 검색", description="키워드와 카테고리로 Hotplace를 검색합니다.")
    @ApiResponse(responseCode="200", description="Hotplace 검색 성공")
    @GetMapping("/search")
    public ResponseEntity<?> searchHotplaces(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer contentTypeId,
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String userId = temporaryUserService.getCurrentUserId(request, response);
            
            Page<Hotplace> pageResult = hotplaceService.searchHotplaces(keyword, contentTypeId, page, size, userId);
            
            List<HotplaceResponseDto> dtoContent = pageResult.getContent().stream()
                    .map(HotplaceResponseDto::from)
                    .collect(Collectors.toList());
            
            PageResponseDto<HotplaceResponseDto> pageResponse = PageResponseDto.<HotplaceResponseDto>builder()
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
            log.error("Hotplace 검색 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "Hotplace 검색 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(summary="인기 Hotplace 조회", description="조회수와 평점 기준 인기 Hotplace를 조회합니다.")
    @ApiResponse(responseCode="200", description="인기 Hotplace 조회 성공")
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularHotplaces(
            @RequestParam(defaultValue="10") int limit,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String userId = temporaryUserService.getCurrentUserId(request, response);
            
            List<Hotplace> hotplaces = hotplaceService.getPopularHotplaces(limit, userId);
            
            List<HotplaceResponseDto> responseDtos = hotplaces.stream()
                    .map(HotplaceResponseDto::from)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responseDtos);
            
        } catch (Exception e) {
            log.error("인기 Hotplace 조회 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "인기 Hotplace 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 에러 응답 생성 헬퍼 메서드
     */
    private Object createErrorResponse(String code, String message) {
        return new ErrorResponse(code, message);
    }
    
    /**
     * 에러 응답 클래스
     */
    private static class ErrorResponse {
        private final String error;
        private final String message;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
        
        public String getError() {
            return error;
        }
        
        public String getMessage() {
            return message;
        }
    }
}