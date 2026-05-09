package com.ncp.team3.crawl.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkingTimeDto(
        String startDate,
        String endDate
) {
}
