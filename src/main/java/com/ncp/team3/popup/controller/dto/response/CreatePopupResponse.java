package com.ncp.team3.popup.controller.dto.response;

import com.ncp.team3.popup.domain.Popup;

public record CreatePopupResponse(
        Long id,
        String title
) {
    public static CreatePopupResponse from(Popup popup) {
        return new CreatePopupResponse(popup.getId(), popup.getTitle());
    }
}
