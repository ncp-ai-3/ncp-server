package com.ncp.team3.chat.controller.dto.response;

import java.util.List;

public record ChatResponseDto(
        String answer,
        List<Long> popupIds
) {
}
