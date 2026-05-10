package com.ncp.team3.authentication.port;

import com.ncp.team3.authentication.domain.MemberOAuth;
import com.ncp.team3.authentication.domain.enums.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberOAuthRepository extends JpaRepository<MemberOAuth, Long> {
    Optional<MemberOAuth> findByProviderAndProviderId(OAuthProvider provider, String providerId);

    boolean existsByMemberIdAndProvider(Long memberId, OAuthProvider provider);
}
