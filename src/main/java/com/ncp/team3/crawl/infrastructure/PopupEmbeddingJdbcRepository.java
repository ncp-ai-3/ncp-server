package com.ncp.team3.crawl.infrastructure;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PopupEmbeddingJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public PopupEmbeddingJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsertEmbedding(Long popupId, String content, List<Double> vector) {
        if (vector == null || vector.isEmpty()) {
            return;
        }

        jdbcTemplate.update("""
                        INSERT INTO popup_embedding (popup_id, content, embedding)
                        VALUES (?, ?, ?::vector)
                        ON CONFLICT (popup_id)
                        DO UPDATE SET
                            content = EXCLUDED.content,
                            embedding = EXCLUDED.embedding,
                            updated_at = now()
                        """,
                popupId,
                content,
                toVectorString(vector)
        );
    }

    private String toVectorString(List<Double> vector) {
        return vector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }
}
