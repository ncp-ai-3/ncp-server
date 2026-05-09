package com.ncp.team3.crawl.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PopplyStoreResponse(
        List<PopplyStoreItem> data
) {
}
