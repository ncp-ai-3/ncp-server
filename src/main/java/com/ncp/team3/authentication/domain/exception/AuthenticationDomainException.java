package com.ncp.team3.authentication.domain.exception;

import com.ncp.team3.global.exception.BusinessException;
import com.ncp.team3.global.exception.constant.Domain;

public class AuthenticationDomainException extends BusinessException {
    public AuthenticationDomainException(AuthenticationErrorCode errorCode) {
        super(Domain.AUTHENTICATION, errorCode);
    }

    public AuthenticationDomainException(AuthenticationErrorCode errorCode, String message) {
        super(Domain.AUTHENTICATION, errorCode, message);
    }
}
