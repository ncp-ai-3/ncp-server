package com.ncp.team3.authentication.controller;

import com.ncp.team3.authentication.controller.dto.request.TokenReissueRequest;
import com.ncp.team3.authentication.controller.dto.response.TokenResponse;
import com.ncp.team3.authentication.service.AuthTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthTokenController {
    private final AuthTokenService authTokenService;

    @PostMapping("/reissue")
    public TokenResponse reissue(@Valid @RequestBody TokenReissueRequest request) {
        return authTokenService.reissue(request.refreshToken());
    }
}
