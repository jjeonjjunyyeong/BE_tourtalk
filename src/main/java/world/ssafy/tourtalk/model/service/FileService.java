package world.ssafy.tourtalk.model.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileService {
	
    // 허용된 업로드 타입 목록
    private static final Set<String> ALLOWED_TYPES = Set.of("curators", "members", "products", "boards");
	
	public String save(MultipartFile file, String type) {
        try {
        	validateType(type); // 업로드 타입 검증
        	
            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.contains(".")) {
                throw new IllegalArgumentException("잘못된 파일명입니다.");
            }

            String ext = originalName.substring(originalName.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            String newFileName = uuid + ext;

            // 절대 경로 기반 저장
            String uploadDir = System.getProperty("user.dir") + "/uploads/" + type;
            Path savePath = Paths.get(uploadDir, newFileName);
            Files.createDirectories(savePath.getParent());

            file.transferTo(savePath.toFile());

            // 상대경로만 반환 (Vue에서 /uploads/** 로 접근 가능)
            return "uploads/" + type + "/" + newFileName;
        } catch (Exception e) {
            log.error("파일 저장 실패: {}", e.getMessage());
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
	
    private void validateType(String type) {
        if (type == null || !ALLOWED_TYPES.contains(type)) {
            throw new IllegalArgumentException("허용되지 않은 업로드 경로입니다: " + type);
        }
    }

}
