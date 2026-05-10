package com.ncp.team3.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncp.team3.authentication.domain.exception.AuthenticationDomainException;
import com.ncp.team3.global.exception.constant.CommonErrorCode;
import com.ncp.team3.global.response.ApiResponse;
import com.ncp.team3.global.response.code.BaseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.ncp.team3.global.security.JwtAuthenticationFilter.JWT_ERROR_ATTRIBUTE;
import static com.ncp.team3.global.security.JwtAuthenticationFilter.JWT_UNKNOWN_ERROR_ATTRIBUTE;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        BaseCode errorCode = resolveErrorCode(request, authException);

        ApiResponse<Object> body = ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), null);

        response.setStatus(errorCode.getHttpStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private BaseCode resolveErrorCode(HttpServletRequest request, AuthenticationException authException) {
        Object jwtError = request.getAttribute(JWT_ERROR_ATTRIBUTE);
        if (jwtError instanceof AuthenticationDomainException domainException) {
            return domainException.getBaseCode();
        }

        Object unknownError = request.getAttribute(JWT_UNKNOWN_ERROR_ATTRIBUTE);
        if (unknownError != null) {
            log.error("[JWT UNKNOWN ERROR]", (Throwable) unknownError);
            return CommonErrorCode.INTERNAL_SERVER_ERROR;
        }

        Throwable cause = authException.getCause();
        if (cause instanceof AuthenticationDomainException domainException) {
            return domainException.getBaseCode();
        }

        return CommonErrorCode.SECURITY_NOT_GIVEN;
    }
}
