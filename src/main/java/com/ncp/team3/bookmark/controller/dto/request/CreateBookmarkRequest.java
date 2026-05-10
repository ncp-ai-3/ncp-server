package com.ncp.team3.bookmark.controller.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateBookmarkRequest(
        @NotNull(message = "팝업 ID는 필수입니다.")
        Long popupId
) {
}
