package com.ncp.team3.authentication.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverProfileResponse(
        String resultcode,
        String message,
        NaverProfile response
) {
    public record NaverProfile(
            String id,
            String email,
            String name,
            String nickname,
            @JsonProperty("profile_image")
            String profileImage,
            String mobile
    ) {
    }
}
