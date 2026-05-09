package com.ncp.team3.global.response.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();
}
