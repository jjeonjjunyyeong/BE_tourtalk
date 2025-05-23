package world.ssafy.tourtalk.model.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.ssafy.tourtalk.model.dto.Hotplace;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.request.hotplace.HotplaceCreateRequestDto;
import world.ssafy.tourtalk.model.mapper.HotplaceMapper;
import world.ssafy.tourtalk.service.FileUploadService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicHotplaceService implements HotplaceService {
    
    private final HotplaceMapper hotplaceMapper;
    private final FileUploadService fileUploadService;
    
    @Override
    @Transactional
    public Hotplace createHotplace(Integer mno, HotplaceCreateRequestDto requestDto) {
        try {
            if (!isValidContentType(requestDto.getContentTypeId())) {
                throw new IllegalArgumentException("존재하지 않는 컨텐츠 타입입니다: " + requestDto.getContentTypeId());
            }
            
            List<String> imageUrls = fileUploadService.uploadImages(requestDto.getImages());
            
            Hotplace hotplace = Hotplace.builder()
                    .mno(mno)  // userId에서 mno로 변경
                    .title(requestDto.getTitle())
                    .latitude(requestDto.getLatitude())
                    .longitude(requestDto.getLongitude())
                    .rating(requestDto.getRating())
                    .contentTypeId(requestDto.getContentTypeId())
                    .description(requestDto.getDescription())
                    .review(requestDto.getReview())
                    .recommendationReason(requestDto.getRecommendationReason())
                    .build();
            
            int insertedCount = hotplaceMapper.insertHotplace(hotplace);
            if (insertedCount == 0) {
                throw new RuntimeException("Hotplace 생성에 실패했습니다.");
            }
            
            for (int i = 0; i < imageUrls.size(); i++) {
                hotplaceMapper.insertHotplaceImage(hotplace.getId(), imageUrls.get(i), i);
            }
            
            return getHotplaceById(hotplace.getId(), mno);
            
        } catch (IOException e) {
            log.error("이미지 업로드 중 오류 발생", e);
            throw new RuntimeException("이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public Hotplace getHotplaceById(Long id, Integer currentMno) {
        Hotplace hotplace = hotplaceMapper.getHotplaceById(id);
        if (hotplace == null) {
            throw new IllegalArgumentException("존재하지 않는 Hotplace입니다: " + id);
        }
        
        hotplaceMapper.updateViewCount(id);
        
        List<String> imageUrls = hotplaceMapper.getHotplaceImages(id);
        hotplace.setImageUrls(imageUrls);
        
        hotplace.setOwner(hotplace.getMno().equals(currentMno));
        
        return hotplace;
    }
    
    @Override
    public Page<Hotplace> getAllHotplaces(int pageNumber, int pageSize, Integer currentMno) {
        int offset = (pageNumber - 1) * pageSize;
        
        List<Hotplace> hotplaces = hotplaceMapper.getAllHotplaces(offset, pageSize);
        
        for (Hotplace hotplace : hotplaces) {
            setAdditionalInfo(hotplace, currentMno);
        }
        
        int totalCount = hotplaceMapper.getTotalHotplacesCount();
        
        return createPage(hotplaces, pageNumber, pageSize, totalCount);
    }
    
    @Override
    public Page<Hotplace> getMyHotplaces(Integer mno, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        
        List<Hotplace> hotplaces = hotplaceMapper.getHotplacesByMno(mno, offset, pageSize);
        
        for (Hotplace hotplace : hotplaces) {
            List<String> imageUrls = hotplaceMapper.getHotplaceImages(hotplace.getId());
            hotplace.setImageUrls(imageUrls);
            hotplace.setOwner(true);
        }
        
        int totalCount = hotplaceMapper.getHotplacesByMnoCount(mno);
        
        return createPage(hotplaces, pageNumber, pageSize, totalCount);
    }
    
    @Override
    @Transactional
    public Hotplace updateHotplace(Long id, Integer mno, HotplaceCreateRequestDto requestDto) {
        try {
            Hotplace existingHotplace = hotplaceMapper.getHotplaceById(id);
            if (existingHotplace == null) {
                throw new IllegalArgumentException("존재하지 않는 Hotplace입니다: " + id);
            }
            
            if (!existingHotplace.getMno().equals(mno)) {
                throw new IllegalArgumentException("수정 권한이 없습니다.");
            }
            
            if (!isValidContentType(requestDto.getContentTypeId())) {
                throw new IllegalArgumentException("존재하지 않는 컨텐츠 타입입니다: " + requestDto.getContentTypeId());
            }
            
            List<String> newImageUrls = null;
            if (requestDto.getImages() != null && !requestDto.getImages().isEmpty()) {
                List<String> oldImageUrls = hotplaceMapper.getHotplaceImages(id);
                for (String oldImageUrl : oldImageUrls) {
                    fileUploadService.deleteImage(oldImageUrl);
                }
                hotplaceMapper.deleteHotplaceImages(id);
                
                newImageUrls = fileUploadService.uploadImages(requestDto.getImages());
            }
            
            Hotplace updatedHotplace = Hotplace.builder()
                    .id(id)
                    .title(requestDto.getTitle())
                    .latitude(requestDto.getLatitude())
                    .longitude(requestDto.getLongitude())
                    .rating(requestDto.getRating())
                    .contentTypeId(requestDto.getContentTypeId())
                    .description(requestDto.getDescription())
                    .review(requestDto.getReview())
                    .recommendationReason(requestDto.getRecommendationReason())
                    .build();
            
            int updatedCount = hotplaceMapper.updateHotplace(updatedHotplace);
            if (updatedCount == 0) {
                throw new RuntimeException("Hotplace 수정에 실패했습니다.");
            }
            
            if (newImageUrls != null) {
                for (int i = 0; i < newImageUrls.size(); i++) {
                    hotplaceMapper.insertHotplaceImage(id, newImageUrls.get(i), i);
                }
            }
            
            return getHotplaceById(id, mno);
            
        } catch (IOException e) {
            log.error("이미지 업로드 중 오류 발생", e);
            throw new RuntimeException("이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteHotplace(Long id, Integer mno) {
        Hotplace existingHotplace = hotplaceMapper.getHotplaceById(id);
        if (existingHotplace == null) {
            throw new IllegalArgumentException("존재하지 않는 Hotplace입니다: " + id);
        }
        
        if (!existingHotplace.getMno().equals(mno)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        
        List<String> imageUrls = hotplaceMapper.getHotplaceImages(id);
        for (String imageUrl : imageUrls) {
            fileUploadService.deleteImage(imageUrl);
        }
        
        hotplaceMapper.deleteHotplaceImages(id);
        
        int deletedCount = hotplaceMapper.deleteHotplace(id);
        return deletedCount > 0;
    }
    
    @Override
    public Page<Hotplace> searchHotplaces(String keyword, Integer contentTypeId, int pageNumber, int pageSize, Integer currentMno) {
        int offset = (pageNumber - 1) * pageSize;
        
        List<Hotplace> hotplaces = hotplaceMapper.searchHotplaces(keyword, contentTypeId, offset, pageSize);
        
        for (Hotplace hotplace : hotplaces) {
            setAdditionalInfo(hotplace, currentMno);
        }
        
        int totalCount = hotplaceMapper.searchHotplacesCount(keyword, contentTypeId);
        
        return createPage(hotplaces, pageNumber, pageSize, totalCount);
    }
    
    @Override
    public List<Hotplace> getPopularHotplaces(int limit, Integer currentMno) {
        List<Hotplace> hotplaces = hotplaceMapper.getPopularHotplaces(limit);
        
        for (Hotplace hotplace : hotplaces) {
            setAdditionalInfo(hotplace, currentMno);
        }
        
        return hotplaces;
    }
    
    @Override
    public boolean isValidContentType(int contentTypeId) {
        return hotplaceMapper.countContentTypeById(contentTypeId) > 0;
    }
    
    @Override
    public boolean isOwner(Long hotplaceId, Integer mno) {
        Hotplace hotplace = hotplaceMapper.getHotplaceById(hotplaceId);
        return hotplace != null && hotplace.getMno().equals(mno);
    }
    
    private void setAdditionalInfo(Hotplace hotplace, Integer currentMno) {
        List<String> imageUrls = hotplaceMapper.getHotplaceImages(hotplace.getId());
        hotplace.setImageUrls(imageUrls);
        
        hotplace.setOwner(hotplace.getMno().equals(currentMno));
    }
    
    private Page<Hotplace> createPage(List<Hotplace> content, int pageNumber, int pageSize, long totalCount) {
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        
        Page<Hotplace> page = Page.<Hotplace>builder()
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