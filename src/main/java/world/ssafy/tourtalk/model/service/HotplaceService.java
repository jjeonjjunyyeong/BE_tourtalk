package world.ssafy.tourtalk.model.service;

import world.ssafy.tourtalk.model.dto.Hotplace;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.request.hotplace.HotplaceCreateRequestDto;

import java.util.List;

public interface HotplaceService {
    
    // Hotplace CRUD
    Hotplace createHotplace(String userId, HotplaceCreateRequestDto requestDto);
    Hotplace getHotplaceById(Long id, String currentUserId);
    Page<Hotplace> getAllHotplaces(int pageNumber, int pageSize, String currentUserId);
    Page<Hotplace> getMyHotplaces(String userId, int pageNumber, int pageSize);
    Hotplace updateHotplace(Long id, String userId, HotplaceCreateRequestDto requestDto);
    boolean deleteHotplace(Long id, String userId);
    
    // 검색
    Page<Hotplace> searchHotplaces(String keyword, Integer contentTypeId, int pageNumber, int pageSize, String currentUserId);
    
    // 인기 hotplace
    List<Hotplace> getPopularHotplaces(int limit, String currentUserId);
    
    // 유효성 검증
    boolean isValidContentType(int contentTypeId);
    boolean isOwner(Long hotplaceId, String userId);
}