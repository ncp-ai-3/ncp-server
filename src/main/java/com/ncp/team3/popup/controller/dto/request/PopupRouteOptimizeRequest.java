package com.ncp.team3.popup.controller.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PopupRouteOptimizeRequest(
        @NotNull(message = "시작 팝업 ID는 필수입니다.")
        Long startPopupId,

        @NotNull(message = "방문할 팝업 ID 목록은 필수입니다.")
        List<Long> targetPopupIds
) {
}
