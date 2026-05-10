package com.ncp.team3.authentication.controller;

import com.ncp.team3.authentication.controller.dto.request.NaverLoginRequest;
import com.ncp.team3.authentication.controller.dto.response.NaverAuthorizationUrlResponse;
import com.ncp.team3.authentication.controller.dto.response.OAuthLoginResponse;
import com.ncp.team3.authentication.usecase.command.NaverLoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증")
public class AuthenticationController {
    private final NaverLoginUseCase naverLoginUseCase;

    @GetMapping("/login/naver/authorization-url")
    @Operation(summary = "네이버 로그인 URL 생성", description = "state를 생성하고 네이버 로그인 URL을 반환합니다.")
    public NaverAuthorizationUrlResponse createNaverAuthorizationUrl() {
        return naverLoginUseCase.createNaverAuthorizationUrl();
    }

    @PostMapping("/login/naver")
    @Operation(summary = "네이버 로그인", description = """
            네이버 인가 코드로 로그인합니다. 신규 회원이면 회원가입 후 JWT를 발급합니다.
            먼저 /api/v1/auth/login/naver/authorization-url 로 state가 포함된 로그인 URL을 발급받아 사용합니다.
            """)
    public OAuthLoginResponse loginWithNaver(@Valid @RequestBody NaverLoginRequest request) {
        return naverLoginUseCase.loginWithNaver(request);
    }
}
