package com.ncp.team3.authentication.domain.exception;

import com.ncp.team3.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthenticationErrorCode implements BaseCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-001", "사용자를 찾을 수 없습니다."),
    WRONG_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH-002", "잘못된 JWT 서명입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT(HttpStatus.UNAUTHORIZED, "AUTH-004", "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "AUTH-005", "JWT 토큰이 유효하지 않습니다."),
    INVALID_OAUTH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-006", "소셜 로그인 토큰이 유효하지 않습니다."),
    OAUTH_TOKEN_EXCHANGE_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-007", "소셜 로그인 토큰 발급에 실패했습니다."),
    OAUTH_PROFILE_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-008", "소셜 로그인 사용자 정보를 찾을 수 없습니다."),
    INVALID_OAUTH_STATE(HttpStatus.UNAUTHORIZED, "AUTH-009", "소셜 로그인 state 값이 유효하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-010", "저장된 refreshToken을 찾을 수 없습니다."),
    REVOKED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-011", "폐기된 refreshToken입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-012", "만료된 refreshToken입니다."),
    REFRESH_TOKEN_MEMBER_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH-013", "refreshToken의 사용자 정보가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
