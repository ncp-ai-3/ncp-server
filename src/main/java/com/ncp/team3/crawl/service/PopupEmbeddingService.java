package com.ncp.team3.crawl.service;

import com.ncp.team3.crawl.domain.PopplyCategory;
import com.ncp.team3.crawl.infrastructure.EmbeddingClient;
import com.ncp.team3.crawl.infrastructure.PopupEmbeddingJdbcRepository;
import com.ncp.team3.popup.domain.Popup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PopupEmbeddingService {
    private final EmbeddingClient embeddingClient;
    private final PopupEmbeddingJdbcRepository popupEmbeddingJdbcRepository;
    private final int embeddingDimension;

    public PopupEmbeddingService(EmbeddingClient embeddingClient,
                                 PopupEmbeddingJdbcRepository popupEmbeddingJdbcRepository,
                                 @Value("${gcp.vertex.embedding-dimension}") int embeddingDimension) {
        this.embeddingClient = embeddingClient;
        this.popupEmbeddingJdbcRepository = popupEmbeddingJdbcRepository;
        this.embeddingDimension = embeddingDimension;
    }

    public boolean createOrUpdateEmbedding(Popup popup, List<String> categoryNames) {
        String content = buildEmbeddingContent(popup, categoryNames);
        List<Double> vector = embeddingClient.embed(content);

        if (!isValidDimension(vector)) {
            log.warn("[EMBEDDING DIMENSION MISMATCH] popupId={}, expected={}, actual={}",
                    popup.getId(), embeddingDimension, vector == null ? 0 : vector.size());
            return false;
        }

        popupEmbeddingJdbcRepository.upsertEmbedding(popup.getId(), content, vector);
        return true;
    }

    public String buildEmbeddingContent(Popup popup, List<String> categoryNames) {
        String categories = normalizedCategoryNames(categoryNames);

        return """
                제목: %s
                브랜드: %s
                설명: %s
                해시태그: %s
                카테고리: %s
                주소: %s
                상태: %s
                """.formatted(
                nullToBlank(popup.getTitle()),
                nullToBlank(popup.getMainBrand()),
                nullToBlank(popup.getDescription()),
                normalizeHashtags(popup.getHashtags()),
                categories,
                nullToBlank(popup.getAddress()),
                nullToBlank(popup.getStatus())
        ).stripTrailing();
    }

    private boolean isValidDimension(List<Double> vector) {
        return vector != null && vector.size() == embeddingDimension;
    }

    private String normalizedCategoryNames(List<String> categoryNames) {
        if (categoryNames == null || categoryNames.isEmpty()) {
            return PopplyCategory.ETC.displayName();
        }

        String categories = categoryNames.stream()
                .filter(categoryName -> categoryName != null && !categoryName.isBlank())
                .map(PopplyCategory::embeddingNameOf)
                .collect(Collectors.joining(", "));

        return categories.isBlank() ? PopplyCategory.ETC.displayName() : categories;
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }

    private String normalizeHashtags(String hashtags) {
        if (hashtags == null || hashtags.isBlank()) {
            return "";
        }

        return hashtags.replace(",", ", ").replaceAll(",\\s+", ", ").trim();
    }
}
