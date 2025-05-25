package world.ssafy.tourtalk.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import world.ssafy.tourtalk.model.dto.enums.ProductStatus;
import world.ssafy.tourtalk.model.dto.enums.Role;
import world.ssafy.tourtalk.model.dto.request.ProductSearchRequest;
import world.ssafy.tourtalk.model.dto.request.SearchConditionRequest;
import world.ssafy.tourtalk.model.dto.request.TourProductRequest;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.dto.response.TourProductResponse;
import world.ssafy.tourtalk.model.service.TourProductService;
import world.ssafy.tourtalk.security.auth.CustomMemberPrincipal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tourProduct")
public class TourProductController {

	private final TourProductService productService;

	@Operation(summary = "상품 등록", description = "학예사(큐레이터) 회원이 상품 정보를 입력하고 등록")
	@PreAuthorize("hasRole('CURATOR')")
	@PostMapping
	public ResponseEntity<?> insert(@AuthenticationPrincipal CustomMemberPrincipal principal,
			@RequestBody TourProductRequest request) {
		try {
			if (principal == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			if (principal.getRole() != Role.CURATOR) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("학예사 계정만 등록할 수 있습니다.");
			}

			log.info("상품 등록 요청: {}", request);
			request.setMno(principal.getMno());
			boolean result = productService.createProduct(request);

			return result ? ResponseEntity.status(HttpStatus.CREATED).body("상품 등록 성공!")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 등록 실패!");

		} catch (DataAccessException e) {
			log.error("상품 등록 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생 : " + e.getMessage());
		}
	}

	@Operation(summary = "상품 수정", description = "등록한 상품의 정보를 수정")
	@PreAuthorize("hasRole('CURATOR')")
	@PutMapping("/{productId}")
	public ResponseEntity<?> update(@AuthenticationPrincipal CustomMemberPrincipal principal,
			@PathVariable int productId, @RequestBody TourProductRequest request) {
		try {
			boolean result = productService.update(productId, request, principal.getMno());

			return result ? ResponseEntity.ok("상품 수정 성공!")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 수정 실패!");
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		} catch (DataAccessException e) {
			log.error("상품 수정 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
		}
	}

	@Operation(summary = "상품 삭제", description = "등록한 상품을 (소프트)삭제")
	@PreAuthorize("hasRole('CURATOR')")
	@PutMapping("/{productId}/deleted")
	public ResponseEntity<?> softDeleted(@AuthenticationPrincipal CustomMemberPrincipal principal,
			@PathVariable int productId) {
		try {
			boolean result = productService.softDeleted(productId, principal.getMno());

			return result ? ResponseEntity.ok("상품이 삭제되었습니다.")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상품 삭제 실패!");
		} catch (DataAccessException e) {
			log.error("상품 삭제 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
		}
	}

	@Operation(summary = "예약페이지 상품 조회", description = "예약페이지 상품 조회")
	@GetMapping("/available")
	public ResponseEntity<?> getAvailbableProducts(@RequestParam(name = "pageNumber", defaultValue = "1") int page,
			@RequestParam(defaultValue = "12") int size) {
		try {
			ProductSearchRequest condition = ProductSearchRequest.builder().pageNumber(page).pageSize(size)
					.status(ProductStatus.OPEN).build();

			condition.setDefaults();
			PageResponse<TourProductResponse> result = productService.searchAvailableProducts(condition);
			return result != null ? ResponseEntity.ok(result)
					: ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록된 상품이 없습니다");
		} catch (DataAccessException e) {
			log.error("예약페이지 로딩 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
		}
	}

	@Operation(summary = "큐레이터 상품 조회", description = "큐레이터 mno 기준으로 상품 조회")
	@PreAuthorize("hasRole('CURATOR')")
	@GetMapping("/curator/products")
	public ResponseEntity<?> getMyProducts(@AuthenticationPrincipal CustomMemberPrincipal principal) {
		try {
			if (principal == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
			}

			List<TourProductResponse> products = productService.getProductsByCurator(principal.getMno());
			return products != null ? ResponseEntity.ok(products)
					: ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록된 상품이 없습니다.");
		} catch (DataAccessException e) {
			log.error("상품 조회 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
		}
	}

	@Operation(summary = "큐레이터 상품 상세 조회", description = "조회된 상품의 상세 정보 조회")
	@GetMapping("/products/{productId}")
	public ResponseEntity<?> getProductById(@PathVariable int productId,
			@AuthenticationPrincipal CustomMemberPrincipal principal) {
		try {
			TourProductResponse product = productService.getProductById(productId);

			if (product.getMno() != principal.getMno()) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
			}

			return product != null ? ResponseEntity.ok(product)
					: ResponseEntity.status(HttpStatus.NOT_FOUND).body("상세보기 할 상품이 없습니다.");
		} catch (DataAccessException e) {
			log.error("상품 조회 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
		}
	}

}