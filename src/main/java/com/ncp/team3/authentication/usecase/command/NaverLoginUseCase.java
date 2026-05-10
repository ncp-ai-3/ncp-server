package com.ncp.team3.authentication.usecase.command;

import com.ncp.team3.authentication.controller.dto.request.NaverLoginRequest;
import com.ncp.team3.authentication.controller.dto.response.NaverAuthorizationUrlResponse;
import com.ncp.team3.authentication.controller.dto.response.OAuthLoginResponse;

public interface NaverLoginUseCase {
    NaverAuthorizationUrlResponse createNaverAuthorizationUrl();

    OAuthLoginResponse loginWithNaver(NaverLoginRequest request);
}
