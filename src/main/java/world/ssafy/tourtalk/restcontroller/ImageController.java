package world.ssafy.tourtalk.restcontroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 이미지 파일 제공 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@Tag(name="Images", description="이미지 파일 제공 API")
public class ImageController {
    
    @Value("${hotplace.upload.path}")
    private String uploadPath;
    
    @Operation(summary="Hotplace 이미지 조회", description="업로드된 Hotplace 이미지 파일을 제공합니다.")
    @ApiResponse(responseCode="200", description="이미지 조회 성공")
    @ApiResponse(responseCode="404", description="이미지를 찾을 수 없음")
    @GetMapping("/hotplaces/{filename:.+}")
    public ResponseEntity<Resource> getHotplaceImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(filename).normalize();
            Resource resource = new FileSystemResource(filePath);
            
            if (!resource.exists() || !resource.isReadable()) {
                log.warn("이미지 파일을 찾을 수 없음: {}", filename);
                return ResponseEntity.notFound().build();
            }
            
            // 파일 확장자에 따른 Content-Type 설정
            String contentType = getContentType(filename);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("이미지 조회 중 오류 발생: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 파일 확장자에 따른 Content-Type 반환
     */
    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            default:
                return "application/octet-stream";
        }
    }
}