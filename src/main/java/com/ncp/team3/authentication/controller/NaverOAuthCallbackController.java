package com.ncp.team3.authentication.controller;

import com.ncp.team3.authentication.controller.dto.request.NaverLoginRequest;
import com.ncp.team3.authentication.controller.dto.response.OAuthLoginResponse;
import com.ncp.team3.authentication.usecase.command.NaverLoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "네이버 OAuth 콜백")
public class NaverOAuthCallbackController {
    private final NaverLoginUseCase naverLoginUseCase;

    @GetMapping("/login/oauth2/code/naver")
    @Operation(summary = "네이버 OAuth 콜백", description = "네이버 로그인 성공 후 전달된 code/state로 회원가입 또는 로그인을 처리합니다.")
    public OAuthLoginResponse callback(@RequestParam String code, @RequestParam String state) {
        return naverLoginUseCase.loginWithNaver(new NaverLoginRequest(code, state));
    }
}
