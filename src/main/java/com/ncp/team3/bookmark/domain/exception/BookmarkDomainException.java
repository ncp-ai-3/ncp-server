package com.ncp.team3.bookmark.domain.exception;

import com.ncp.team3.global.exception.BusinessException;
import com.ncp.team3.global.exception.constant.Domain;

public class BookmarkDomainException extends BusinessException {
    public BookmarkDomainException(BookmarkErrorCode errorCode) {
        super(Domain.BOOKMARK, errorCode);
    }

    public BookmarkDomainException(BookmarkErrorCode errorCode, String message) {
        super(Domain.BOOKMARK, errorCode, message);
    }
}
