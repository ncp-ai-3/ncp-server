package com.ncp.team3.popup.service;

import com.ncp.team3.popup.controller.dto.response.GetPopupDetailResponse;
import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.domain.exception.PopupDomainException;
import com.ncp.team3.popup.domain.exception.PopupErrorCode;
import com.ncp.team3.popup.port.PopupRepository;
import com.ncp.team3.popup.usecase.query.GetPopupDetailUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupQueryService implements GetPopupDetailUseCase {
    private final PopupRepository popupRepository;

    @Override
    public GetPopupDetailResponse getPopupDetail(Long popupId) {
        Popup popup = popupRepository.findDetailById(popupId)
                .orElseThrow(() -> new PopupDomainException(PopupErrorCode.POPUP_NOT_FOUND));

        return GetPopupDetailResponse.from(popup);
    }
}
