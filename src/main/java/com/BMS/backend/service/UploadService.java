package com.BMS.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

/**
 * 파일 업로드 및 다운로드 서비스
 * - 사용자가 직접 업로드하는 파일 처리
 * - DALL-E API에서 생성된 이미지를 URL에서 다운로드하여 로컬에 저장
 */
@Service
@Slf4j
public class UploadService {

    private final String uploadDir = "uploads/";

    /**
     * 사용자가 직접 업로드한 파일 저장
     */
    public String upload(MultipartFile file) {
        try {
            // 업로드 폴더 없으면 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일명 생성 (UUID + 원본이름)
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);

            // 파일 저장
            file.transferTo(dest);

            // 프론트가 접근할 수 있는 URL 반환
            return "/uploads/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
        }
    }

    /**
     * URL에서 이미지를 다운로드하여 로컬에 저장 (DALL-E API용)
     *
     * @param imageUrl DALL-E가 반환한 임시 이미지 URL
     * @return 로컬에 저장된 파일 경로 (/uploads/xxx_dalle_cover.png)
     */
    public String downloadAndSave(String imageUrl) {
        try {
            log.info("🔽 이미지 다운로드 시작: {}", imageUrl);

            // 업로드 폴더 없으면 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일명 생성 (UUID + dalle_cover.png)
            String fileName = UUID.randomUUID() + "_dalle_cover.png";
            File destFile = new File(uploadDir + fileName);

            // URL에서 이미지 다운로드
            URL url = new URL(imageUrl);
            try (InputStream in = url.openStream();
                 FileOutputStream out = new FileOutputStream(destFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            log.info("✅ 이미지 저장 완료: {}", destFile.getAbsolutePath());

            // 프론트가 접근할 수 있는 URL 반환
            return "/uploads/" + fileName;

        } catch (IOException e) {
            log.error("❌ 이미지 다운로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("이미지 다운로드 및 저장 실패: " + e.getMessage());
        }
    }
}