package com.ncp.team3.authentication.service;

import com.ncp.team3.authentication.controller.dto.response.TokenResponse;
import com.ncp.team3.authentication.domain.RefreshToken;
import com.ncp.team3.authentication.domain.exception.AuthenticationDomainException;
import com.ncp.team3.authentication.domain.exception.AuthenticationErrorCode;
import com.ncp.team3.authentication.port.RefreshTokenRepository;
import com.ncp.team3.global.security.JwtTokenProvider;
import com.ncp.team3.member.domain.Member;
import com.ncp.team3.member.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenResponse issueTokens(Member member) {
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        refreshTokenRepository.findAllByMemberIdAndRevokedFalse(member.getId())
                .forEach(RefreshToken::revoke);

        refreshTokenRepository.save(RefreshToken.create(
                member.getId(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpirationDateTime(refreshToken)
        ));

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse reissue(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            throw new AuthenticationDomainException(AuthenticationErrorCode.INVALID_JWT, "refreshToken은 필수입니다.");
        }

        jwtTokenProvider.validateRefreshToken(refreshTokenValue);

        RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new AuthenticationDomainException(AuthenticationErrorCode.REFRESH_TOKEN_NOT_FOUND));

        validateSavedRefreshToken(savedRefreshToken, refreshTokenValue);

        savedRefreshToken.revoke();

        Member member = memberRepository.findById(savedRefreshToken.getMemberId())
                .orElseThrow(() -> new AuthenticationDomainException(AuthenticationErrorCode.MEMBER_NOT_FOUND));

        return issueTokens(member);
    }

    private void validateSavedRefreshToken(RefreshToken savedRefreshToken, String refreshTokenValue) {
        if (savedRefreshToken.isRevoked()) {
            throw new AuthenticationDomainException(AuthenticationErrorCode.REVOKED_REFRESH_TOKEN);
        }

        if (savedRefreshToken.isExpired()) {
            throw new AuthenticationDomainException(AuthenticationErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        Long jwtMemberId = jwtTokenProvider.parseRefreshToken(refreshTokenValue);
        if (!savedRefreshToken.getMemberId().equals(jwtMemberId)) {
            throw new AuthenticationDomainException(AuthenticationErrorCode.REFRESH_TOKEN_MEMBER_MISMATCH);
        }
    }
}
