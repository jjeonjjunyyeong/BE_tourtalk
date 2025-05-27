package world.ssafy.tourtalk.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import world.ssafy.tourtalk.model.dto.Hotplace;

import java.util.List;

@Mapper
public interface HotplaceMapper {
    
    // Hotplace CRUD
    int insertHotplace(Hotplace hotplace);
    Hotplace getHotplaceById(@Param("id") Long id);
    List<Hotplace> getAllHotplaces(@Param("offset") int offset, @Param("limit") int limit);
    List<Hotplace> getHotplacesByMno(@Param("mno") Integer mno, @Param("offset") int offset, @Param("limit") int limit);
    int updateHotplace(Hotplace hotplace);
    int deleteHotplace(@Param("id") Long id);
    
    // 조회수 업데이트
    int updateViewCount(@Param("id") Long id);
    
    // 통계
    int getTotalHotplacesCount();
    int getHotplacesByMnoCount(@Param("mno") Integer mno);
    
    // 검색
    List<Hotplace> searchHotplaces(@Param("keyword") String keyword, @Param("contentTypeId") Integer contentTypeId, 
                                  @Param("offset") int offset, @Param("limit") int limit);
    int searchHotplacesCount(@Param("keyword") String keyword, @Param("contentTypeId") Integer contentTypeId);
    
    // 인기 hotplace
    List<Hotplace> getPopularHotplaces(@Param("limit") int limit);
    
    // 이미지 관리
    int insertHotplaceImage(@Param("hotplaceId") Long hotplaceId, @Param("imageUrl") String imageUrl, @Param("imageOrder") int imageOrder);
    List<String> getHotplaceImages(@Param("hotplaceId") Long hotplaceId);
    int deleteHotplaceImages(@Param("hotplaceId") Long hotplaceId);
    
    // 유효성 검증
    int countContentTypeById(@Param("contentTypeId") int contentTypeId);
}