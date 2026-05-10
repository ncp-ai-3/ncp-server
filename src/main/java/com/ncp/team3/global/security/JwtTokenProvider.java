package com.ncp.team3.global.security;

import com.ncp.team3.authentication.domain.exception.AuthenticationDomainException;
import com.ncp.team3.authentication.domain.exception.AuthenticationErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";

    private final SecretKey accessTokenSecret;
    private final SecretKey refreshTokenSecret;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.access-token-secret}") String accessTokenSecret,
            @Value("${jwt.refresh-token-secret}") String refreshTokenSecret,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds
    ) {
        this.accessTokenSecret = Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshTokenSecret = Keys.hmacShaKeyFor(refreshTokenSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
    }

    public String createAccessToken(Long memberId, String role) {
        Date now = new Date();
        Date validityDate = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim(AUTHORITIES_KEY, List.of(role))
                .issuedAt(now)
                .expiration(validityDate)
                .signWith(accessTokenSecret)
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        Date validityDate = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(validityDate)
                .signWith(refreshTokenSecret)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessTokenSecret);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshTokenSecret);
    }

    public Long parseAccessToken(String token) {
        return Long.parseLong(parseClaims(token, accessTokenSecret).getSubject());
    }

    public Long parseRefreshToken(String token) {
        validateToken(token, refreshTokenSecret);
        return Long.parseLong(parseClaims(token, refreshTokenSecret).getSubject());
    }

    public LocalDateTime getRefreshTokenExpirationDateTime(String token) {
        Date expiration = parseClaims(token, refreshTokenSecret).getExpiration();
        return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromAccessToken(String token) {
        Object roles = parseClaims(token, accessTokenSecret).get(AUTHORITIES_KEY);
        if (roles instanceof List<?>) {
            return (List<String>) roles;
        }
        return Collections.emptyList();
    }

    private boolean validateToken(String token, SecretKey secretKey) {
        try {
            parseClaims(token, secretKey);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw new AuthenticationDomainException(AuthenticationErrorCode.WRONG_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw new AuthenticationDomainException(AuthenticationErrorCode.EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw new AuthenticationDomainException(AuthenticationErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw new AuthenticationDomainException(AuthenticationErrorCode.INVALID_JWT);
        }
    }

    private Claims parseClaims(String token, SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
