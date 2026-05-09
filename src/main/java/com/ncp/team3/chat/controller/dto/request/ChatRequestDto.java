package com.ncp.team3.chat.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatRequestDto(
        @NotNull(message = "회원 ID는 필수입니다.")
        Long userId,

        @NotBlank(message = "질문은 필수입니다.")
        String question
) {
}
