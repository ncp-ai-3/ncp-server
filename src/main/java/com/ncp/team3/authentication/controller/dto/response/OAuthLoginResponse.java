package com.ncp.team3.authentication.controller.dto.response;

import com.ncp.team3.authentication.domain.enums.OAuthProvider;

public record OAuthLoginResponse(
        Long memberId,
        String accessToken,
        String refreshToken,
        String tokenType,
        OAuthProvider provider,
        boolean newMember
) {
    public static OAuthLoginResponse of(Long memberId,
                                        String accessToken,
                                        String refreshToken,
                                        OAuthProvider provider,
                                        boolean newMember) {
        return new OAuthLoginResponse(memberId, accessToken, refreshToken, "Bearer", provider, newMember);
    }
}
