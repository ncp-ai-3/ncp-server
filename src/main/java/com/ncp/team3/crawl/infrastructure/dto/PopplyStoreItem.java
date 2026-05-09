package com.ncp.team3.crawl.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PopplyStoreItem(
        Long storeId,
        Integer categoryId,
        String thumbnails,
        String name,
        String title,
        String mainBrand,
        String topLevelAddress,
        String address,
        String detailAddress,
        Double latitude,
        Double longitude,
        String startDate,
        String endDate,
        String workingTime,
        String preRegisterLink,
        String hashtag,
        String status,
        PopplyStoreDetail storeDetail
) {
}
