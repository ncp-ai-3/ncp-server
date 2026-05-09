package com.ncp.team3.member.domain.exception;

import com.ncp.team3.global.exception.BusinessException;
import com.ncp.team3.global.exception.constant.Domain;

public class MemberDomainException extends BusinessException {
    public MemberDomainException(MemberErrorCode errorCode) {
        super(Domain.MEMBER, errorCode);
    }

    public MemberDomainException(MemberErrorCode errorCode, String message) {
        super(Domain.MEMBER, errorCode, message);
    }
}
