package world.ssafy.tourtalk.model.service;

import world.ssafy.tourtalk.model.dto.Hotplace;
import world.ssafy.tourtalk.model.dto.Page;
import world.ssafy.tourtalk.model.dto.request.hotplace.HotplaceCreateRequestDto;

import java.util.List;

public interface HotplaceService {
    
    // Hotplace CRUD
    Hotplace createHotplace(Integer mno, HotplaceCreateRequestDto requestDto);
    Hotplace getHotplaceById(Long id, Integer currentMno);
    Page<Hotplace> getAllHotplaces(int pageNumber, int pageSize, Integer currentMno);
    Page<Hotplace> getMyHotplaces(Integer mno, int pageNumber, int pageSize);
    Hotplace updateHotplace(Long id, Integer mno, HotplaceCreateRequestDto requestDto);
    boolean deleteHotplace(Long id, Integer mno);
    
    // 검색
    Page<Hotplace> searchHotplaces(String keyword, Integer contentTypeId, int pageNumber, int pageSize, Integer currentMno);
    
    // 인기 hotplace
    List<Hotplace> getPopularHotplaces(int limit, Integer currentMno);
    
    // 유효성 검증
    boolean isValidContentType(int contentTypeId);
    boolean isOwner(Long hotplaceId, Integer mno);
}