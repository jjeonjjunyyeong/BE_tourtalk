package world.ssafy.tourtalk.model.service;

import java.nio.file.AccessDeniedException;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import world.ssafy.tourtalk.model.dto.request.TourProductRequest;
import world.ssafy.tourtalk.model.dto.response.TourProductResponse;
import world.ssafy.tourtalk.model.mapper.TourProductMapper;

@Service
@RequiredArgsConstructor
public class TourProductService {

	private final TourProductMapper productMapper;
	
	public boolean insert(TourProductRequest request, int mno) {
		return productMapper.insert(request, mno) > 0;
	}

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

	public boolean softDeleted(int productId, int mno) {
		TourProductResponse product = productMapper.getById(productId);
		if(product == null || product.getMno() != mno) return false;
		
		return productMapper.softDeleted(productId) > 0;
	}

}
