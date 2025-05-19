package world.ssafy.tourtalk.restcontroller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import world.ssafy.tourtalk.controller.RestControllerHelper;
import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.request.attraction.AttractionCreateUpdateRequestDto;
import world.ssafy.tourtalk.model.dto.response.attraction.AttractionResponseDto;
import world.ssafy.tourtalk.model.service.AttractionService;

/**
 * 관리자용 관광지 관리 REST 컨트롤러
 * API 키 인증 필요
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/attractions")
@Tag(name="Admin Attractions", description="관리자용 관광지 관리 API")
@SecurityRequirement(name = "Admin API Key")
@RequiredArgsConstructor
public class AdminAttractionRestController implements RestControllerHelper {
    
    private final AttractionService attractionService;
    
    @Operation(summary="관광지 생성", description="새로운 관광지를 생성합니다. API 키 필요.")
    @ApiResponse(responseCode="201", description="관광지 생성 성공")
    @ApiResponse(responseCode="400", description="잘못된 요청 데이터")
    @ApiResponse(responseCode="401", description="인증 실패")
    @ApiResponse(responseCode="409", description="중복된 관광지")
    @PostMapping
    public ResponseEntity<?> createAttraction(@Valid @RequestBody AttractionCreateUpdateRequestDto requestDto) {
        try {
            log.info("관광지 생성 요청: {}", requestDto.getTitle());
            
            Attraction createdAttraction = attractionService.createAttraction(requestDto);
            AttractionResponseDto responseDto = AttractionResponseDto.from(createdAttraction);
            
            log.info("관광지 생성 완료: ID={}, Title={}", createdAttraction.getNo(), createdAttraction.getTitle());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
            
        } catch (IllegalArgumentException e) {
            log.warn("관광지 생성 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("BAD_REQUEST", e.getMessage()));
        } catch (Exception e) {
            log.error("관광지 생성 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "관광지 생성 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(summary="관광지 수정", description="기존 관광지 정보를 수정합니다. API 키 필요.")
    @ApiResponse(responseCode="200", description="관광지 수정 성공")
    @ApiResponse(responseCode="400", description="잘못된 요청 데이터")
    @ApiResponse(responseCode="401", description="인증 실패")
    @ApiResponse(responseCode="404", description="관광지를 찾을 수 없음")
    @PutMapping("/{no}")
    public ResponseEntity<?> updateAttraction(
            @PathVariable Integer no,
            @Valid @RequestBody AttractionCreateUpdateRequestDto requestDto) {
        try {
            log.info("관광지 수정 요청: ID={}, Title={}", no, requestDto.getTitle());
            
            Attraction updatedAttraction = attractionService.updateAttraction(no, requestDto);
            AttractionResponseDto responseDto = AttractionResponseDto.from(updatedAttraction);
            
            log.info("관광지 수정 완료: ID={}, Title={}", updatedAttraction.getNo(), updatedAttraction.getTitle());
            return ResponseEntity.ok(responseDto);
            
        } catch (IllegalArgumentException e) {
            log.warn("관광지 수정 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("BAD_REQUEST", e.getMessage()));
        } catch (Exception e) {
            log.error("관광지 수정 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "관광지 수정 중 오류가 발생했습니다."));
        }
    }
    
    @Operation(summary="관광지 삭제", description="관광지를 삭제합니다. API 키 필요.")
    @ApiResponse(responseCode="204", description="관광지 삭제 성공")
    @ApiResponse(responseCode="401", description="인증 실패")
    @ApiResponse(responseCode="404", description="관광지를 찾을 수 없음")
    @DeleteMapping("/{no}")
    public ResponseEntity<?> deleteAttraction(@PathVariable Integer no) {
        try {
            log.info("관광지 삭제 요청: ID={}", no);
            
            boolean deleted = attractionService.deleteAttraction(no);
            if (deleted) {
                log.info("관광지 삭제 완료: ID={}", no);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("관광지 삭제 실패: ID={}", no);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("DELETE_FAILED", "관광지 삭제에 실패했습니다."));
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("관광지 삭제 실패 - 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            log.error("관광지 삭제 중 예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("INTERNAL_ERROR", "관광지 삭제 중 오류가 발생했습니다."));
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