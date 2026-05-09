package com.ncp.team3.popup.domain.exception;

import com.ncp.team3.global.response.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PopupErrorCode implements BaseCode {

    POPUP_NOT_FOUND(HttpStatus.NOT_FOUND, "POPUP-001", "팝업을 찾을 수 없습니다."),

    INVALID_POPUP_IMAGE_URL(HttpStatus.BAD_REQUEST, "POPUP-002", "팝업 이미지 URL이 유효하지 않습니다."),
    INVALID_POPUP_TITLE(HttpStatus.BAD_REQUEST, "POPUP-003", "팝업 제목이 유효하지 않습니다."),
    INVALID_POPUP_DESCRIPTION(HttpStatus.BAD_REQUEST, "POPUP-004", "팝업 설명이 유효하지 않습니다."),
    INVALID_POPUP_ADDRESS(HttpStatus.BAD_REQUEST, "POPUP-005", "팝업 주소가 유효하지 않습니다."),
    INVALID_POPUP_LOCATION(HttpStatus.BAD_REQUEST, "POPUP-006", "팝업 위치 정보가 유효하지 않습니다."),
    INVALID_POPUP_ORIGIN_ID(HttpStatus.BAD_REQUEST, "POPUP-007", "오리지널 팝업 ID가 유효하지 않습니다."),
    INVALID_POPUP_DATE(HttpStatus.BAD_REQUEST, "POPUP-008", "팝업 운영 기간이 유효하지 않습니다."),
    INVALID_POPUP_TIME(HttpStatus.BAD_REQUEST, "POPUP-009", "팝업 운영 시간이 유효하지 않습니다."),
    INVALID_POPUP_RESERVATION_URL(HttpStatus.BAD_REQUEST, "POPUP-010", "팝업 예약 URL이 유효하지 않습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "POPUP-011", "카테고리를 찾을 수 없습니다."),
    POPUP_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "POPUP-012", "팝업 카테고리 정보를 찾을 수 없습니다."),
    INVALID_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "POPUP-013", "카테고리명이 유효하지 않습니다."),
    INVALID_POPUP_CATEGORY_POPUP(HttpStatus.BAD_REQUEST, "POPUP-014", "팝업 카테고리의 팝업 정보가 유효하지 않습니다."),
    INVALID_POPUP_CATEGORY_CATEGORY(HttpStatus.BAD_REQUEST, "POPUP-015", "팝업 카테고리의 카테고리 정보가 유효하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
