package com.ncp.team3.chat.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestDto(
        @NotBlank(message = "질문은 필수입니다.")
        String question
) {
}
