package com.ncp.team3.popup.domain.exception;

import com.ncp.team3.global.exception.BusinessException;
import com.ncp.team3.global.exception.constant.Domain;

public class PopupDomainException extends BusinessException {
    public PopupDomainException(PopupErrorCode errorCode) {
        super(Domain.POPUP, errorCode);
    }

    public PopupDomainException(PopupErrorCode errorCode, String message) {
        super(Domain.POPUP, errorCode, message);
    }
}
