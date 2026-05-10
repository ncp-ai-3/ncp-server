package com.ncp.team3.authentication.service;

import com.ncp.team3.authentication.controller.dto.request.NaverLoginRequest;
import com.ncp.team3.authentication.controller.dto.response.NaverAuthorizationUrlResponse;
import com.ncp.team3.authentication.controller.dto.response.OAuthLoginResponse;
import com.ncp.team3.authentication.controller.dto.response.TokenResponse;
import com.ncp.team3.authentication.domain.MemberOAuth;
import com.ncp.team3.authentication.domain.enums.OAuthProvider;
import com.ncp.team3.authentication.domain.exception.AuthenticationDomainException;
import com.ncp.team3.authentication.domain.exception.AuthenticationErrorCode;
import com.ncp.team3.authentication.infrastructure.NaverOAuthClient;
import com.ncp.team3.authentication.infrastructure.dto.NaverProfileResponse;
import com.ncp.team3.authentication.infrastructure.dto.NaverTokenResponse;
import com.ncp.team3.authentication.port.MemberOAuthRepository;
import com.ncp.team3.authentication.usecase.command.NaverLoginUseCase;
import com.ncp.team3.member.domain.Member;
import com.ncp.team3.member.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationCommandService implements NaverLoginUseCase {
    private final MemberRepository memberRepository;
    private final MemberOAuthRepository memberOAuthRepository;
    private final NaverOAuthClient naverOAuthClient;
    private final OAuthStateStore oAuthStateStore;
    private final AuthTokenService authTokenService;

    @Override
    public NaverAuthorizationUrlResponse createNaverAuthorizationUrl() {
        String state = oAuthStateStore.create();
        return new NaverAuthorizationUrlResponse(naverOAuthClient.authorizationUrl(state), state);
    }

    @Override
    @Transactional
    public OAuthLoginResponse loginWithNaver(NaverLoginRequest request) {
        oAuthStateStore.verifyAndRemove(request.state());
        NaverTokenResponse tokenResponse = naverOAuthClient.exchangeAuthorizationCode(request.code(), request.state());
        NaverProfileResponse.NaverProfile profile = naverOAuthClient.fetchProfile(tokenResponse.accessToken());

        if (profile.email() == null || profile.email().isBlank()) {
            throw new AuthenticationDomainException(
                    AuthenticationErrorCode.OAUTH_PROFILE_NOT_FOUND,
                    "네이버 프로필에서 이메일을 가져올 수 없습니다."
            );
        }

        return memberOAuthRepository.findByProviderAndProviderId(OAuthProvider.NAVER, profile.id())
                .map(memberOAuth -> loginExistingNaverMember(memberOAuth, profile))
                .orElseGet(() -> signUpOrLinkNaverMember(profile));
    }

    private OAuthLoginResponse signUpOrLinkNaverMember(NaverProfileResponse.NaverProfile profile) {
        Optional<Member> existingMember = memberRepository.findByEmail(profile.email());
        boolean newMember = existingMember.isEmpty();
        Member member = existingMember.orElseGet(() -> {
            Member createdMember = Member.createSocial(profile.email(), profile.name(), profile.profileImage());
            return memberRepository.save(createdMember);
        });

        if (!memberOAuthRepository.existsByMemberIdAndProvider(member.getId(), OAuthProvider.NAVER)) {
            memberOAuthRepository.save(MemberOAuth.create(member.getId(), OAuthProvider.NAVER, profile.id()));
        }

        return issueOAuthLoginResponse(member, OAuthProvider.NAVER, newMember);
    }

    private OAuthLoginResponse loginExistingNaverMember(MemberOAuth memberOAuth, NaverProfileResponse.NaverProfile profile) {
        Member member = memberRepository.findById(memberOAuth.getMemberId())
                .orElse(null);

        if (member == null) {
            memberOAuthRepository.delete(memberOAuth);
            return signUpOrLinkNaverMember(profile);
        }

        return issueOAuthLoginResponse(member, OAuthProvider.NAVER, false);
    }

    private OAuthLoginResponse issueOAuthLoginResponse(Member member, OAuthProvider provider, boolean newMember) {
        TokenResponse tokenResponse = authTokenService.issueTokens(member);

        return OAuthLoginResponse.of(member.getId(), tokenResponse.accessToken(), tokenResponse.refreshToken(), provider, newMember);
    }
}
