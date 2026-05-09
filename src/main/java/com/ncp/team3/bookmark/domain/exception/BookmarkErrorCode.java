package com.ncp.team3.bookmark.domain.exception;

import com.ncp.team3.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BookmarkErrorCode implements BaseCode {

    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK-001", "북마크를 찾을 수 없습니다."),

    INVALID_BOOKMARK_MEMBER(HttpStatus.BAD_REQUEST, "BOOKMARK-002", "북마크 회원 정보가 유효하지 않습니다."),
    INVALID_BOOKMARK_POPUP(HttpStatus.BAD_REQUEST, "BOOKMARK-003", "북마크 팝업 정보가 유효하지 않습니다."),
    BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "BOOKMARK-004", "이미 등록된 북마크입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
