package com.ncp.team3.member.domain.exception;

import com.ncp.team3.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-001", "회원을 찾을 수 없습니다."),

    INVALID_MEMBER_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER-002", "회원 이메일이 유효하지 않습니다."),
    INVALID_MEMBER_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER-003", "회원 비밀번호가 유효하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
