package com.ncp.team3.authentication.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NaverLoginRequest(
        @NotBlank(message = "네이버 인가 코드는 필수입니다.")
        String code,

        @NotBlank(message = "네이버 state 값은 필수입니다.")
        String state
) {
}
