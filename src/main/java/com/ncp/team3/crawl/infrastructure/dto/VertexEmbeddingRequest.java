package com.ncp.team3.crawl.infrastructure.dto;

import java.util.List;

public record VertexEmbeddingRequest(
        List<VertexEmbeddingInstance> instances
) {
    public static VertexEmbeddingRequest of(String content) {
        return new VertexEmbeddingRequest(List.of(new VertexEmbeddingInstance(content)));
    }

    public record VertexEmbeddingInstance(
            String content
    ) {
    }
}
