package com.ncp.team3.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증")
public class AuthenticationController {
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 진행합니다.")
    public void login(Long memberId) {
    }

}
