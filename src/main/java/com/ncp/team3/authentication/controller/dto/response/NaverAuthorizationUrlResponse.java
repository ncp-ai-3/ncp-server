package com.ncp.team3.authentication.controller.dto.response;

public record NaverAuthorizationUrlResponse(
        String authorizationUrl,
        String state
) {
}
