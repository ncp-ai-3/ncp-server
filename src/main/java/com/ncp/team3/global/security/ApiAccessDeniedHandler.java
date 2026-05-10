package com.ncp.team3.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncp.team3.global.exception.constant.CommonErrorCode;
import com.ncp.team3.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ApiAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        CommonErrorCode errorCode = CommonErrorCode.SECURITY_FORBIDDEN;
        ApiResponse<Object> body = ApiResponse.onFailure(
                errorCode.getCode(),
                errorCode.getMessage(),
                accessDeniedException.getMessage()
        );

        response.setStatus(errorCode.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
