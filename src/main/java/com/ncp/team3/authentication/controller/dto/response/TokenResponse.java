package com.ncp.team3.authentication.controller.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
