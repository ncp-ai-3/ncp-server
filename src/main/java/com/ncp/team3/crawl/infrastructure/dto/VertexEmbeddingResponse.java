package com.ncp.team3.crawl.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VertexEmbeddingResponse(
        List<Prediction> predictions
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Prediction(
            Embeddings embeddings
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Embeddings(
            List<Double> values
    ) {
    }
}
