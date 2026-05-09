package com.ncp.team3.crawl.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PopplyStoreDetail(
        String contents,
        String brandUrl
) {
}
