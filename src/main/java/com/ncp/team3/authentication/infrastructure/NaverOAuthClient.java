package com.ncp.team3.authentication.infrastructure;

import com.ncp.team3.authentication.domain.exception.AuthenticationDomainException;
import com.ncp.team3.authentication.domain.exception.AuthenticationErrorCode;
import com.ncp.team3.authentication.infrastructure.dto.NaverProfileResponse;
import com.ncp.team3.authentication.infrastructure.dto.NaverTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Slf4j
@Component
public class NaverOAuthClient {
    private static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String PROFILE_URL = "https://openapi.naver.com/v1/nid/me";

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public NaverOAuthClient(RestTemplateBuilder restTemplateBuilder,
                            @Value("${oauth.naver.client-id}") String clientId,
                            @Value("${oauth.naver.client-secret}") String clientSecret,
                            @Value("${oauth.naver.redirect-uri}") String redirectUri) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public String authorizationUrl(String state) {
        return UriComponentsBuilder.fromHttpUrl("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .toUriString();
    }

    public NaverTokenResponse exchangeAuthorizationCode(String code, String state) {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new AuthenticationDomainException(
                    AuthenticationErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED,
                    "네이버 OAuth client-id/client-secret 설정이 필요합니다."
            );
        }

        String uri = UriComponentsBuilder.fromHttpUrl(TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("state", state)
                .toUriString();

        try {
            NaverTokenResponse response = restTemplate.getForObject(uri, NaverTokenResponse.class);
            if (response == null || response.accessToken() == null || response.accessToken().isBlank()) {
                throw new AuthenticationDomainException(AuthenticationErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED);
            }
            return response;
        } catch (AuthenticationDomainException e) {
            throw e;
        } catch (RestClientException e) {
            log.warn("[NAVER TOKEN EXCHANGE FAILED] {}", e.getMessage());
            throw new AuthenticationDomainException(AuthenticationErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED);
        }
    }

    public NaverProfileResponse.NaverProfile fetchProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<NaverProfileResponse> response = restTemplate.exchange(
                    PROFILE_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    NaverProfileResponse.class
            );

            NaverProfileResponse body = response.getBody();
            if (body == null || body.response() == null || body.response().id() == null) {
                throw new AuthenticationDomainException(AuthenticationErrorCode.OAUTH_PROFILE_NOT_FOUND);
            }

            return body.response();
        } catch (AuthenticationDomainException e) {
            throw e;
        } catch (RestClientException e) {
            log.warn("[NAVER PROFILE FETCH FAILED] {}", e.getMessage());
            throw new AuthenticationDomainException(AuthenticationErrorCode.INVALID_OAUTH_TOKEN);
        }
    }
}
