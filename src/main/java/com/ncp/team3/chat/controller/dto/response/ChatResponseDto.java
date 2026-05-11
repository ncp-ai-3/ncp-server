package com.ncp.team3.chat.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record ChatResponseDto(
        @JsonAlias("final_answer")
        String answer,
        @JsonAlias({"popup_ids", "recommended_popup_ids"})
        List<Long> popupIds,
        @JsonProperty("retrieved_popups")
        JsonNode retrievedPopups
) {
    public ChatResponseDto {
        if ((popupIds == null || popupIds.isEmpty()) && retrievedPopups != null) {
            popupIds = extractPopupIds(retrievedPopups);
        }
    }

    public ChatResponseDto(String answer, List<Long> popupIds) {
        this(answer, popupIds, null);
    }

    @JsonIgnore
    public boolean hasPopupIds() {
        return popupIds != null && !popupIds.isEmpty();
    }

    private static List<Long> extractPopupIds(JsonNode retrievedPopups) {
        Set<Long> popupIds = new LinkedHashSet<>();
        collectPopupIds(retrievedPopups, popupIds);
        return new ArrayList<>(popupIds);
    }

    private static void collectPopupIds(JsonNode node, Set<Long> popupIds) {
        if (node == null || node.isNull()) {
            return;
        }

        if (node.isArray()) {
            for (JsonNode item : node) {
                collectPopupIds(item, popupIds);
            }
            return;
        }

        Long popupId = asLong(node);
        if (popupId != null) {
            popupIds.add(popupId);
            return;
        }

        if (node.isObject()) {
            addIfPresent(node, popupIds, "popupId");
            addIfPresent(node, popupIds, "popup_id");
            addIfPresent(node, popupIds, "id");

            collectPopupIds(node.get("popup"), popupIds);
            collectPopupIds(node.get("metadata"), popupIds);
        }
    }

    private static void addIfPresent(JsonNode node, Set<Long> popupIds, String fieldName) {
        Long popupId = asLong(node.get(fieldName));
        if (popupId != null) {
            popupIds.add(popupId);
        }
    }

    private static Long asLong(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        if (node.canConvertToLong()) {
            return node.asLong();
        }

        if (node.isTextual()) {
            try {
                return Long.parseLong(node.asText());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }
}
