package com.ncp.team3.global.logging;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class LogMaskingUtils {
    private static final int DEFAULT_PREVIEW_LENGTH = 1000;
    private static final String MASK = "***";
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "authorization",
            "accesstoken",
            "access_token",
            "refreshtoken",
            "refresh_token",
            "token",
            "password",
            "secret",
            "clientsecret",
            "client_secret",
            "apikey",
            "api_key",
            "key",
            "code",
            "cookie"
    );

    private LogMaskingUtils() {
    }

    public static String maskQuery(String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return null;
        }

        return Arrays.stream(queryString.split("&"))
                .map(LogMaskingUtils::maskQueryParam)
                .collect(Collectors.joining("&"));
    }

    public static String bodyPreview(String body) {
        return bodyPreview(body, DEFAULT_PREVIEW_LENGTH);
    }

    public static String bodyPreview(String body, int maxLength) {
        if (body == null) {
            return "";
        }

        String masked = maskSensitiveValues(body);
        if (masked.length() <= maxLength) {
            return masked;
        }
        return masked.substring(0, maxLength) + "...";
    }

    public static String maskSensitiveValues(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        String masked = value;
        for (String key : SENSITIVE_KEYS) {
            masked = masked.replaceAll("(?i)(\"" + key + "\"\\s*:\\s*\")([^\"]*)(\")", "$1" + MASK + "$3");
            masked = masked.replaceAll("(?i)(\\b" + key + "\\b\\s*[=:]\\s*)([^,\\s&}]+)", "$1" + MASK);
        }
        return masked;
    }

    private static String maskQueryParam(String param) {
        int separatorIndex = param.indexOf('=');
        if (separatorIndex < 0) {
            return isSensitiveKey(decode(param)) ? encode(param) + "=" + MASK : param;
        }

        String key = param.substring(0, separatorIndex);
        if (isSensitiveKey(decode(key))) {
            return key + "=" + MASK;
        }
        return param;
    }

    private static boolean isSensitiveKey(String key) {
        if (key == null) {
            return false;
        }

        String normalized = key.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
        return SENSITIVE_KEYS.stream()
                .map(sensitiveKey -> sensitiveKey.replace("_", ""))
                .anyMatch(normalized::contains);
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return value;
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
