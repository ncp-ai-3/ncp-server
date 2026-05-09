package com.ncp.team3.popup.usecase.query;

import com.ncp.team3.popup.controller.dto.response.GetPopupDetailResponse;

public interface GetPopupDetailUseCase {
    GetPopupDetailResponse getPopupDetail(Long popupId);
}
