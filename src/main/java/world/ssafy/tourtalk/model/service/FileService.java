package world.ssafy.tourtalk.model.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileService {
	
	public String save(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.contains(".")) {
                throw new IllegalArgumentException("잘못된 파일명입니다.");
            }

            String ext = originalName.substring(originalName.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            String newFileName = uuid + ext;

            // 절대 경로 기반 저장
            String uploadDir = System.getProperty("user.dir") + "/uploads/curators";
            Path savePath = Paths.get(uploadDir, newFileName);
            Files.createDirectories(savePath.getParent());

            file.transferTo(savePath.toFile());

            // 상대경로만 반환 (Vue에서 /uploads/** 로 접근 가능)
            return "uploads/curators/" + newFileName;
        } catch (Exception e) {
            log.error("파일 저장 실패: {}", e.getMessage());
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

}
