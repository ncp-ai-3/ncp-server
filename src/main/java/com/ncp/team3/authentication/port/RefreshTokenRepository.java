package com.ncp.team3.authentication.port;

import com.ncp.team3.authentication.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByMemberIdAndRevokedFalse(Long memberId);

    Optional<RefreshToken> findByMemberIdAndRevokedFalse(Long memberId);
}
