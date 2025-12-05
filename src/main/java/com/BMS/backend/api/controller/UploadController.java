package com.BMS.backend.api.controller;

import com.BMS.backend.global.ApiResponse;
import com.BMS.backend.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// /api/v1/books/{id}/cover 요청 받음
// MultipartFile 형태로 파일 받음
// UploadService에 파일 저장 요청
// 저장 url을 프론트에 json 형태로 반환 (ApiResponse 사용)




@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class UploadController {

    private final UploadService uploadService;

    @PutMapping("/{id}/cover")
    public ResponseEntity<?> uploadCover(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {

        // 파일 업로드
        String url = uploadService.upload(file);

        // bookService.updateCover(id, url);   ← 이건 2번 개발자가 구현해야 함

        return ResponseEntity.ok(ApiResponse.ok(
                String.format("업로드 성공: %s", url)
        ));
    }
}