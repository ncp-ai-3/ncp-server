package com.ncp.team3.crawl.config;

import com.ncp.team3.crawl.infrastructure.EmbeddingClient;
import com.ncp.team3.crawl.infrastructure.RagEmbeddingClient;
import com.ncp.team3.crawl.infrastructure.RagEmbeddingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(RagEmbeddingProperties.class)
public class EmbeddingClientConfiguration {

    @Bean
    public EmbeddingClient embeddingClient(RestTemplateBuilder restTemplateBuilder,
                                           RagEmbeddingProperties ragEmbeddingProperties) {
        if (!StringUtils.hasText(ragEmbeddingProperties.getBaseUrl())) {
            throw new IllegalStateException(
                    "rag.embedding.base-url 이 비어 있습니다. RAG_EMBEDDING_BASE_URL 또는 application.yaml 의 rag.embedding.base-url 을 설정하세요."
            );
        }
        return new RagEmbeddingClient(restTemplateBuilder, ragEmbeddingProperties);
    }
}
