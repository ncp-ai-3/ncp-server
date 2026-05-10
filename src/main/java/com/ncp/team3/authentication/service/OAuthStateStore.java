package com.ncp.team3.authentication.service;

import com.ncp.team3.authentication.domain.exception.AuthenticationDomainException;
import com.ncp.team3.authentication.domain.exception.AuthenticationErrorCode;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OAuthStateStore {
    private static final Duration STATE_TTL = Duration.ofMinutes(5);

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, Instant> states = new ConcurrentHashMap<>();

    public String create() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String state = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        states.put(state, Instant.now().plus(STATE_TTL));
        return state;
    }

    public void verifyAndRemove(String state) {
        Instant expiresAt = states.remove(state);
        if (expiresAt == null || expiresAt.isBefore(Instant.now())) {
            throw new AuthenticationDomainException(AuthenticationErrorCode.INVALID_OAUTH_STATE);
        }
    }
}
