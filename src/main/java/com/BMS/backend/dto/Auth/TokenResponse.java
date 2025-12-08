package com.BMS.backend.dto.Auth;

import lombok.Getter;

@Getter
public class TokenResponse {

    private String token;
    private Long userId;

    public TokenResponse(String token,  Long userId) {
        this.token = token;
        this.userId = userId;
    }
}
