package com.ncp.team3.crawl.infrastructure;

import com.ncp.team3.crawl.infrastructure.dto.RagEmbeddingApiRequest;
import com.ncp.team3.crawl.infrastructure.dto.RagEmbeddingApiResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

public class RagEmbeddingClient implements EmbeddingClient {

    private static final int ERROR_BODY_MAX = 400;

    private final RestTemplate restTemplate;
    private final String requestUrl;
    private final String apiKey;

    public RagEmbeddingClient(RestTemplateBuilder restTemplateBuilder, RagEmbeddingProperties properties) {
        this(
                restTemplateBuilder
                        .setConnectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
                        .setReadTimeout(Duration.ofSeconds(properties.getReadTimeoutSeconds()))
                        .build(),
                properties
        );
    }

    /**
     * 동일 패키지 테스트에서 {@link RestTemplate} 에 {@link org.springframework.test.web.client.MockRestServiceServer} 를 묶을 때 사용합니다.
     */
    RagEmbeddingClient(RestTemplate restTemplate, RagEmbeddingProperties properties) {
        if (!StringUtils.hasText(properties.getBaseUrl())) {
            throw new IllegalArgumentException("rag.embedding.base-url must not be blank");
        }
        this.restTemplate = restTemplate;
        this.requestUrl = buildRequestUrl(properties.getBaseUrl(), properties.getPath());
        this.apiKey = StringUtils.hasText(properties.getApiKey()) ? properties.getApiKey().trim() : null;
    }

    private static String buildRequestUrl(String baseUrl, String path) {
        String base = baseUrl.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String p = !StringUtils.hasText(path) ? "/api/v1/embed" : path.trim();
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        return base + p;
    }

    @Override
    public List<Double> embed(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null) {
            headers.setBearerAuth(apiKey);
        }

        try {
            RagEmbeddingApiResponse response = restTemplate.postForObject(
                    requestUrl,
                    new HttpEntity<>(new RagEmbeddingApiRequest(content), headers),
                    RagEmbeddingApiResponse.class
            );

            if (response == null || response.embedding() == null || response.embedding().isEmpty()) {
                throw new IllegalStateException("RAG 임베딩 응답이 비어 있습니다.");
            }

            return response.embedding();
        } catch (RestClientException e) {
            throw new IllegalStateException("RAG 임베딩 API 호출에 실패했습니다. " + describeFailure(e), e);
        }
    }

    private String describeFailure(RestClientException e) {
        if (e instanceof RestClientResponseException responseException) {
            String body = responseException.getResponseBodyAsString();
            if (body != null && body.length() > ERROR_BODY_MAX) {
                body = body.substring(0, ERROR_BODY_MAX) + "...";
            }
            return "url=%s status=%s body=%s"
                    .formatted(requestUrl, responseException.getStatusCode(), body == null ? "" : body);
        }
        return "url=%s message=%s".formatted(requestUrl, e.getMessage());
    }
}
