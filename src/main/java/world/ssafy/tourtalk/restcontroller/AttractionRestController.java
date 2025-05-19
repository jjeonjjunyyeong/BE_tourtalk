package world.ssafy.tourtalk.restcontroller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.controller.RestControllerHelper;
import world.ssafy.tourtalk.model.dto.Attraction;
import world.ssafy.tourtalk.model.dto.AttractionDetail;
import world.ssafy.tourtalk.model.dto.AttractionForm;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.SearchCondition;
import world.ssafy.tourtalk.model.dto.request.attraction.AttractionSearchRequestDto;
import world.ssafy.tourtalk.model.dto.response.attraction.AttractionDetailResponseDto;
import world.ssafy.tourtalk.model.dto.response.attraction.AttractionFormResponseDto;
import world.ssafy.tourtalk.model.dto.response.attraction.AttractionResponseDto;
import world.ssafy.tourtalk.model.dto.response.common.PageResponseDto;
import world.ssafy.tourtalk.model.service.AttractionService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/attractions")
@OpenAPIDefinition(info = @Info(title="Attractions API", version = "1.0", description="관광지 정보 조회 API"))
@Tag(name="Attractions", description="관광지 정보 조회 API")
@RequiredArgsConstructor
public class AttractionRestController implements RestControllerHelper {
    
    private final AttractionService attractionService;
    
    @Value("${api.key_sgis_service_id}")
    private String keySgisServiceId;
    
    @Value("${api.key_sgis_security}")
    private String keySgisSecurity;
    
    @Value("${api.key_vworld}")
    private String keyVworld;
    
    @Value("${api.key_data}")
    private String keyData;
    
    @Operation(summary="ID로 관광지 조회", description="관광지 상세 정보와 근처 관광지 조회")
    @ApiResponse(responseCode="200", description="관광지 상세 정보 조회 성공")
    @ApiResponse(responseCode="404", description="관광지를 찾을 수 없음")
    @GetMapping("/{no}")
    public ResponseEntity<?> getAttractionByNo(@PathVariable Integer no) {
        try {
            Attraction detailAttraction = attractionService.getAttractionByNo(no);
            
            if(detailAttraction == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("관광지를 찾을 수 없습니다.");
            }
            
            // 조회수 증가
            attractionService.updateViewCount(no);
            
            // 근처 관광지 조회 - 코드 값 직접 활용
            List<Attraction> nearAttractionList = attractionService.getAttractionsByDirectCodes(
                detailAttraction.getContentTypeId(), 
                detailAttraction.getSidoCode(),
                detailAttraction.getGugunCode()
            );
            
            // 배열로 변환 (최대 5개)
            Attraction[] nearAttractionArr = new Attraction[0];
            if (nearAttractionList != null && !nearAttractionList.isEmpty()) {
                // 현재 관광지는 제외
                nearAttractionList.removeIf(a -> a.getNo() == detailAttraction.getNo());
                
                int size = Math.min(nearAttractionList.size(), 5);
                nearAttractionArr = nearAttractionList.subList(0, size).toArray(new Attraction[0]);
            }
            
            AttractionDetail response = new AttractionDetail(detailAttraction, nearAttractionArr);
            AttractionDetailResponseDto responseDto = AttractionDetailResponseDto.from(response);
            
            return ResponseEntity.ok(responseDto);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("관광지 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary="코드 기반 관광지 목록 조회", description="코드 값을 기반으로 페이지네이션된 관광지 목록 반환")
    @ApiResponse(
        responseCode="200", 
        description="관광지 목록 조회 성공",
        content=@Content(schema=@Schema(implementation=PageResponseDto.class))
    )
    @GetMapping("/codes")
    public ResponseEntity<?> getAttractionsByDirectCodes(
            @RequestParam Integer contentTypeId,
            @RequestParam Integer sidoCode,
            @RequestParam Integer gugunCode,
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size) {
        try {
            Page<Attraction> pageResult = attractionService.getAttractionsByDirectCodesWithPaging(
                contentTypeId, sidoCode, gugunCode, page, size);
            
            // Page<Attraction>을 Page<AttractionResponseDto>로 변환
            List<AttractionResponseDto> dtoContent = pageResult.getContent().stream()
                    .map(AttractionResponseDto::from)
                    .collect(Collectors.toList());
            
            PageResponseDto<AttractionResponseDto> response = PageResponseDto.<AttractionResponseDto>builder()
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
            
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("관광지 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary="동적 검색 조건으로 관광지 조회", description="다양한 검색 조건으로 관광지 목록 조회")
    @ApiResponse(
        responseCode="200", 
        description="관광지 검색 성공",
        content=@Content(schema=@Schema(implementation=PageResponseDto.class))
    )
    @GetMapping("/search")
    public ResponseEntity<?> searchAttractions(@Valid AttractionSearchRequestDto requestDto) {
        try {
            SearchCondition condition = requestDto.toSearchCondition();
            Page<Attraction> pageResult = attractionService.searchAttractionsByCodes(condition);
            
            // Page<Attraction>을 Page<AttractionResponseDto>로 변환
            List<AttractionResponseDto> dtoContent = pageResult.getContent().stream()
                    .map(AttractionResponseDto::from)
                    .collect(Collectors.toList());
            
            PageResponseDto<AttractionResponseDto> response = PageResponseDto.<AttractionResponseDto>builder()
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
            
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("관광지 검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary="기존 방식으로 관광지 목록 조회", description="이름 기반 관광지 목록 조회")
    @ApiResponse(
        responseCode="200", 
        description="관광지 목록 조회 성공",
        content=@Content(schema=@Schema(implementation=PageResponseDto.class))
    )
    @Deprecated
    @GetMapping
    public ResponseEntity<?> getAttractionByAddress(
            @RequestParam String contentTypeName,
            @RequestParam("sido") String areaCode,
            @RequestParam("gugun") String siGunGuCode,
            @RequestParam(defaultValue="1") int page,
            @RequestParam(defaultValue="10") int size) {
        
        try {
            // 검색 조건 생성
            SearchCondition condition = new SearchCondition();
            condition.setPageNumber(page);
            condition.setPageSize(size);
            condition.setKeyword(null);
            
            // 코드 변환 후 직접 코드 기반 검색 (변환 로직은 서비스에서 처리)
            int contentTypeId = attractionService.getContentTypeIdByName(contentTypeName);
            int sidoCode = attractionService.getSidoCodeByName(areaCode);
            int gugunCode = attractionService.getGugunCodeByName(siGunGuCode, sidoCode);
            
            // 코드 기반 검색 호출
            Page<Attraction> pageResult = attractionService.getAttractionsByDirectCodesWithPaging(
                contentTypeId, sidoCode, gugunCode, page, size);
            
            // Page<Attraction>을 Page<AttractionResponseDto>로 변환
            List<AttractionResponseDto> dtoContent = pageResult.getContent().stream()
                    .map(AttractionResponseDto::from)
                    .collect(Collectors.toList());
            
            PageResponseDto<AttractionResponseDto> response = PageResponseDto.<AttractionResponseDto>builder()
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
            
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("관광지 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary="관광지 폼 데이터 조회", description="컨텐츠 유형, 시도 목록, 랜덤 관광지 반환")
    @ApiResponse(
        responseCode="200", 
        description="데이터 조회 성공",
        content=@Content(schema=@Schema(implementation=AttractionFormResponseDto.class))
    )
    @GetMapping("/form-data")
    public ResponseEntity<?> getAttractionFormData() {
        try {
            List<Map<String, Object>> contentList = attractionService.getContent();
            List<Map<String, Object>> sidoList = attractionService.getSido();
            List<Attraction> randomAttractions = attractionService.getRandomAttractions(6);
            
            AttractionForm form = new AttractionForm(contentList, sidoList, randomAttractions);
            AttractionFormResponseDto response = AttractionFormResponseDto.from(form);
            
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("폼 데이터 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary="시도별 시군구 목록 조회", description="주어진 시도에 대한 시군구 목록 반환")
    @ApiResponse(responseCode="200", description="시군구 목록 조회 성공")
    @GetMapping("/guguns")
    public ResponseEntity<?> getGugunList(@RequestParam("sidoCode") String sidoCode) {
        try {
            List<Map<String, Object>> gugunList = attractionService.getGugun(sidoCode);
            return ResponseEntity.ok(gugunList);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("시군구 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary="조회수 높은 관광지 목록 조회", description="조회수 기준 정렬된 관광지 목록 반환")
    @ApiResponse(responseCode="200", description="인기 관광지 목록 조회 성공")
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularAttractions(
            @RequestParam(defaultValue="10") int limit) {
        try {
            // 조회수 기준 관광지 조회
            List<Map<String, Object>> popularAttractions = attractionService.allCountView();
            
            // 결과 제한
            if (popularAttractions.size() > limit) {
                popularAttractions = popularAttractions.subList(0, limit);
            }
            
            return ResponseEntity.ok(popularAttractions);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("인기 관광지 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary="테마별 랜덤 관광지 조회", description="특정 컨텐츠 유형의 랜덤 관광지 목록 반환")
    @ApiResponse(responseCode="200", description="테마별 관광지 조회 성공")
    @GetMapping("/random/theme/{contentTypeId}")
    public ResponseEntity<?> getRandomAttractionsByTheme(
            @PathVariable Integer contentTypeId,
            @RequestParam(defaultValue="6") int count) {
        try {
            List<Attraction> attractions = attractionService.getRandomAttractionsByTheme(count, contentTypeId);
            
            List<AttractionResponseDto> response = attractions.stream()
                    .map(AttractionResponseDto::from)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("테마별 관광지 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Operation(summary="지역별 랜덤 관광지 조회", description="특정 지역의 랜덤 관광지 목록 반환")
    @ApiResponse(responseCode="200", description="지역별 관광지 조회 성공")
    @GetMapping("/random/region/{sidoCode}")
    public ResponseEntity<?> getRandomAttractionsByRegion(
            @PathVariable Integer sidoCode,
            @RequestParam(defaultValue="6") int count) {
        try {
            List<Attraction> attractions = attractionService.getRandomAttractionsByRegion(count, sidoCode);
            
            List<AttractionResponseDto> response = attractions.stream()
                    .map(AttractionResponseDto::from)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("지역별 관광지 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}