package com.ncp.team3.global.logging;

import com.ncp.team3.global.security.MemberPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class AccessLogFilter extends OncePerRequestFilter {
    private static final Set<String> EXCLUDED_PREFIXES = Set.of(
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator",
            "/favicon.ico"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return EXCLUDED_PREFIXES.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = LogMaskingUtils.maskQuery(request.getQueryString());
        String userId = currentUserId();

        log.info("[HTTP REQUEST] method={} uri={} query={} clientIp={} userId={}",
                method, uri, query, clientIp(request), userId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;
            log.info("[HTTP RESPONSE] method={} uri={} status={} durationMs={} userId={}",
                    method, uri, response.getStatus(), durationMs, currentUserId());
        }
    }

    private String currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof MemberPrincipal memberPrincipal) {
            return String.valueOf(memberPrincipal.getMemberId());
        }

        if (principal instanceof String principalText && !"anonymousUser".equals(principalText)) {
            return principalText;
        }

        return "anonymous";
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
