package world.ssafy.tourtalk.model.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.request.ProductSearchRequest;
import world.ssafy.tourtalk.model.dto.request.TourProductRequest;
import world.ssafy.tourtalk.model.dto.response.PageResponse;
import world.ssafy.tourtalk.model.dto.response.TourProductResponse;
import world.ssafy.tourtalk.model.mapper.TourProductMapper;

@Service
@RequiredArgsConstructor
public class TourProductService {

	private final TourProductMapper productMapper;
	
    @Transactional
    public boolean createProduct(TourProductRequest request) {
        int result = productMapper.insertProduct(request);
        if (result == 0) return false;
        
        int productId = request.getProductId();
        boolean allTimeSlotsInserted = true;
        
        if (request.getTimeSlots() != null && !request.getTimeSlots().isEmpty()) {
            for (String time : request.getTimeSlots()) {
                int inserted = productMapper.insertTimeSlot(productId, time);
                if (inserted == 0) {
                    allTimeSlotsInserted = false;
                    break;
                }
            }
        }

        return allTimeSlotsInserted;
    }

    @Transactional
	public boolean update(int productId, TourProductRequest request, int mno) throws NotFoundException, AccessDeniedException {
		TourProductResponse product = productMapper.getById(productId);
		
		if (product == null) {
		    throw new NotFoundException("상품을 찾을 수 없습니다");
		}
		if (product.getMno() != mno) {
		    throw new AccessDeniedException("수정 권한이 없습니다");
		}
		
		return productMapper.update(productId, request, mno) > 0;
	}

    @Transactional
	public boolean softDeleted(int productId, int mno) {
		TourProductResponse product = productMapper.getById(productId);
		if(product == null || product.getMno() != mno) return false;
		
		return productMapper.softDeleted(productId) > 0;
	}

	public List<TourProductResponse> getProductsByCurator(int mno) {
    	List<TourProductResponse> products = productMapper.findByCurator(mno);

		List<TourProductResponse> result = new ArrayList<>();

		for (TourProductResponse product : products) {
			List<LocalTime> timeSlots = productMapper.selectTimeSlots(product.getProductId());

			result.add(TourProductResponse.builder()
				.productId(product.getProductId())
				.mno(product.getMno())
				.locationNo(product.getLocationNo())
				.title(product.getTitle())
				.description(product.getDescription())
				.maxParticipants(product.getMaxParticipants())
				.minParticipants(product.getMinParticipants())
				.priceType(product.getPriceType())
				.price(product.getPrice())
				.startDate(product.getStartDate())
				.status(product.getStatus())
				.thumbnailImg(product.getThumbnailImg())
				.tags(product.getTags())
				.meetingPlace(product.getMeetingPlace())
				.meetingTime(product.getMeetingTime()) 
				.duration(product.getDuration())
				.createdAt(product.getCreatedAt())
				.updatedAt(product.getUpdatedAt())
				.timeSlots(timeSlots != null ? timeSlots : List.of())
				.build());
		}

		return result;
	}

	public TourProductResponse getProductById(int productId) {
		 TourProductResponse product = productMapper.getById(productId);
		    if (product != null) {
		        List<LocalTime> timeSlots = productMapper.selectTimeSlots(productId);
		        product.setTimeSlots(timeSlots);
		    }
		    return product;
	}

	
	@Transactional(readOnly = true)
	public PageResponse<TourProductResponse> searchAvailableProducts(ProductSearchRequest condition) {
		int total = productMapper.countAvailableProducts(condition);
		if (total == 0) {
            return null;
        }

        List<TourProductResponse> list = productMapper.findAvailableProducts(condition);

        return PageResponse.<TourProductResponse>builder()
                .content(list)
                .pageNumber(condition.getPageNumber())
                .pageSize(condition.getPageSize())
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / condition.getPageSize()))
                .first(condition.getPageNumber() == 1)
                .last(condition.getPageNumber() * condition.getPageSize() >= total)
                .build();
	}
	
}