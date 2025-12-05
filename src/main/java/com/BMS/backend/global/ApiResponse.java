package com.BMS.backend.global;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String error;

    // 성공 응답
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // 실패 응답
    public static ApiResponse<?> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}