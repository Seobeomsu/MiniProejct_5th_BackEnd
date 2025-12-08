package com.BMS.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;   // ResponseStatusException 추가 (AuthService의 ResponseStatusException에 대응)

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 글로벌 예외 처리기
 * 모든 Controller에서 발생하는 예외를 여기서 일괄 처리합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    // Custom Error 처리한 부분 처리기
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.error(e.getMessage(), e.getHttpStatus().value()));
    }

    // Valid Error 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Failed");
        errorResponse.put("message", "입력값 검증에 실패했습니다.");
        errorResponse.put("fieldErrors", fieldErrors);

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    // 기타 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
    /**
    * 401 UNAUTHRORIZED - 대응
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", ex.getStatusCode().value());
        errorResponse.put("error", ex.getStatusCode().toString());
        errorResponse.put("message", ex.getReason());

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(errorResponse);
    }
}
