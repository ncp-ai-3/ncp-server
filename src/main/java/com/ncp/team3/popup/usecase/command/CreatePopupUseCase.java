package com.ncp.team3.popup.usecase.command;

import com.ncp.team3.popup.controller.dto.request.CreatePopupRequest;
import com.ncp.team3.popup.controller.dto.response.CreatePopupResponse;

public interface CreatePopupUseCase {
    CreatePopupResponse createPopup(CreatePopupRequest request);
}
