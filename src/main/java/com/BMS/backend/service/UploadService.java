package com.BMS.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.UUID;

// 파일 저장 경로를 자동으로 생성하고,
// 파일 이름 충돌 방지(UUID)하고,
// 로컬 디스크에 이미지 저장,
// 저장된 URL을 호출한 Controller에게 넘긴다.


@Service
public class UploadService {

    private final String uploadDir = "uploads/";

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
}