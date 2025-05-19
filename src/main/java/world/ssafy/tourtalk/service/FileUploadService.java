package world.ssafy.tourtalk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 파일 업로드 관리 서비스
 */
@Slf4j
@Service
public class FileUploadService {
    
    @Value("${hotplace.upload.path}")
    private String uploadPath;
    
    @Value("${hotplace.image.base-url}")
    private String baseUrl;
    
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    /**
     * 다중 이미지 파일 업로드
     */
    public List<String> uploadImages(List<MultipartFile> files) throws IOException {
        List<String> uploadedUrls = new ArrayList<>();
        
        if (files == null || files.isEmpty()) {
            return uploadedUrls;
        }
        
        // 업로드 디렉토리 생성
        createUploadDirectory();
        
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String uploadedUrl = uploadSingleImage(file);
                uploadedUrls.add(uploadedUrl);
            }
        }
        
        return uploadedUrls;
    }
    
    /**
     * 단일 이미지 파일 업로드
     */
    private String uploadSingleImage(MultipartFile file) throws IOException {
        // 파일 유효성 검증
        validateFile(file);
        
        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String newFilename = String.format("hotplace_%s_%s.%s", timestamp, uuid, extension);
        
        // 파일 저장
        Path targetPath = Paths.get(uploadPath, newFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("파일 업로드 완료: {}", newFilename);
        
        // 접근 가능한 URL 반환
        return baseUrl + "/" + newFilename;
    }
    
    /**
     * 파일 유효성 검증
     */
    private void validateFile(MultipartFile file) throws IOException {
        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 5MB까지 업로드 가능합니다.");
        }
        
        // 파일 확장자 검증
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. jpg, jpeg, png, gif만 업로드 가능합니다.");
        }
    }
    
    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    /**
     * 업로드 디렉토리 생성
     */
    private void createUploadDirectory() throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
            log.info("업로드 디렉토리 생성: {}", uploadPath);
        }
    }
    
    /**
     * 파일 삭제
     */
    public boolean deleteImage(String imageUrl) {
        try {
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadPath, filename);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 완료: {}", filename);
                return true;
            }
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", imageUrl, e);
        }
        return false;
    }
}