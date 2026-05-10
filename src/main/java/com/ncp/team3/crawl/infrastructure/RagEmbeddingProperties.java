package com.ncp.team3.crawl.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rag.embedding")
public class RagEmbeddingProperties {

    /**
     * RAG 서버 베이스 URL (예: http://localhost:8001). 비어 있지 않게 설정하면 동기 벡터 API가 사용됩니다.
     */
    private String baseUrl;

    private String path = "/api/v1/embed";

    private int connectTimeoutSeconds = 5;

    private int readTimeoutSeconds = 120;

    /**
     * 설정 시 Authorization: Bearer 로 전송합니다.
     */
    private String apiKey;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public void setConnectTimeoutSeconds(int connectTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public void setReadTimeoutSeconds(int readTimeoutSeconds) {
        this.readTimeoutSeconds = readTimeoutSeconds;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
