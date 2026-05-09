package com.ncp.team3.crawl.infrastructure;

import com.google.auth.oauth2.GoogleCredentials;
import com.ncp.team3.crawl.infrastructure.dto.VertexEmbeddingRequest;
import com.ncp.team3.crawl.infrastructure.dto.VertexEmbeddingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class VertexEmbeddingClient implements EmbeddingClient {
    private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/cloud-platform");

    private final RestTemplate restTemplate;
    private final String projectId;
    private final String location;
    private final String model;

    public VertexEmbeddingClient(RestTemplateBuilder restTemplateBuilder,
                                 @Value("${gcp.project-id}") String projectId,
                                 @Value("${gcp.location}") String location,
                                 @Value("${gcp.vertex.embedding-model}") String model) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
        this.projectId = projectId;
        this.location = location;
        this.model = model;
    }

    @Override
    public List<Double> embed(String content) {
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalStateException("GCP project-id 설정이 필요합니다.");
        }

        String endpoint = "https://%s-aiplatform.googleapis.com/v1/projects/%s/locations/%s/publishers/google/models/%s:predict"
                .formatted(location, projectId, location, model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken());

        try {
            VertexEmbeddingResponse response = restTemplate.postForObject(
                    endpoint,
                    new HttpEntity<>(VertexEmbeddingRequest.of(content), headers),
                    VertexEmbeddingResponse.class
            );

            if (response == null || response.predictions() == null || response.predictions().isEmpty()) {
                throw new IllegalStateException("Vertex AI 임베딩 응답이 비어 있습니다.");
            }

            VertexEmbeddingResponse.Embeddings embeddings = response.predictions().get(0).embeddings();
            if (embeddings == null || embeddings.values() == null || embeddings.values().isEmpty()) {
                throw new IllegalStateException("Vertex AI 임베딩 values가 비어 있습니다.");
            }

            return embeddings.values();
        } catch (RestClientException e) {
            throw new IllegalStateException("Vertex AI 임베딩 API 호출에 실패했습니다.", e);
        }
    }

    private String accessToken() {
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                    .createScoped(SCOPES);
            credentials.refreshIfExpired();
            return credentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new IllegalStateException("GCP 인증 정보를 가져올 수 없습니다.", e);
        }
    }
}
