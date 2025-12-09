package com.BMS.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAI DALL-E API를 사용하여 책 표지 이미지를 생성하고 로컬에 저장하는 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CoverGenerationService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model:dall-e-3}")
    private String model;

    @Value("${openai.image-size:1024x1024}")
    private String imageSize;

    private final UploadService uploadService;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/images/generations";

    /**
     * 책 제목과 저자로 표지 이미지 생성 및 로컬 저장
     *
     * @param title 책 제목
     * @param author 저자
     * @return 로컬에 저장된 이미지 URL (/uploads/xxx.png)
     */
    public String generateAndSaveBookCover(String title, String author) {
        String dalleImageUrl = null;

        try {
            // 1️⃣ DALL-E API로 이미지 생성
            log.info("📘 표지 생성 시작: '{}' by {}", title, author);
            dalleImageUrl = callDalleApi(title, author);

            // 2️⃣ 생성된 이미지를 로컬에 다운로드 및 저장
            log.info("💾 이미지 다운로드 및 로컬 저장 중: {}", dalleImageUrl);
            String localImageUrl = uploadService.downloadAndSave(dalleImageUrl);

            log.info("✅ 표지 생성 완료: {}", localImageUrl);
            return localImageUrl;

        } catch (HttpClientErrorException e) {
            // OpenAI API 에러 (401, 429, 500 등)
            log.error("❌ OpenAI API 에러 [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("OpenAI API 키가 유효하지 않습니다. 설정을 확인해주세요.");
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new RuntimeException("OpenAI API 요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요.");
            } else {
                throw new RuntimeException("OpenAI API 오류가 발생했습니다: " + e.getMessage());
            }

        } catch (Exception e) {
            log.error("❌ 표지 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("표지 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * DALL-E API 호출하여 이미지 URL 받기 (내부 메서드)
     *
     * @param title 책 제목
     * @param author 저자
     * @return DALL-E가 생성한 임시 이미지 URL
     */
    private String callDalleApi(String title, String author) {
        try {
            // 프롬프트 생성
            String prompt = createPrompt(title, author);
            log.info("🎨 DALL-E 프롬프트: {}", prompt);

            // OpenAI API 요청 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 요청 바디
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", prompt);
            requestBody.put("n", 1);  // 이미지 1개 생성
            requestBody.put("size", imageSize);
            requestBody.put("quality", "standard");  // standard 또는 hd

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    OPENAI_API_URL,
                    request,
                    Map.class
            );

            // 응답 파싱
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, String>> data = (List<Map<String, String>>) response.getBody().get("data");
                if (data != null && !data.isEmpty()) {
                    String imageUrl = data.get(0).get("url");
                    log.info("🎉 DALL-E 이미지 URL 수신: {}", imageUrl);
                    return imageUrl;
                }
            }

            throw new RuntimeException("DALL-E API 응답이 올바르지 않습니다");

        } catch (HttpClientErrorException e) {
            // HTTP 에러는 상위로 전파
            throw e;
        } catch (Exception e) {
            log.error("DALL-E API 호출 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("DALL-E API 호출 실패: " + e.getMessage());
        }
    }

    /**
     * 책 제목과 저자를 기반으로 DALL-E 프롬프트 생성
     *
     * @param title 책 제목
     * @param author 저자
     * @return DALL-E 프롬프트
     */
    private String createPrompt(String title, String author) {
        return String.format(
                "Create a professional and elegant book cover design for a book titled '%s' by %s. " +
                "The design should be modern, visually appealing, and suitable for a library or bookstore. " +
                "Include artistic typography for the title and author name. " +
                "Use colors and imagery that match the book's theme and mood. " +
                "Make it look like a real published book cover.",
                title, author
        );
    }
}
