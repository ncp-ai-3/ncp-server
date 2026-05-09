package com.ncp.team3.chat.controller.dto.response;

import com.ncp.team3.popup.controller.dto.response.GetPopupDetailResponse;

import java.util.List;

public record ClientResponseDto(
        String answer,
        List<GetPopupDetailResponse> recommendedPopups
) {
}
