package com.ncp.team3.member.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMemberRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
        String email,

        @Size(max = 100, message = "이름은 100자 이하여야 합니다.")
        String name,

        @Size(max = 500, message = "이미지 URL은 500자 이하여야 합니다.")
        String imageUrl
) {
}
