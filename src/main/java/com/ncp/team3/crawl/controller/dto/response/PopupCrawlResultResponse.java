package com.ncp.team3.crawl.controller.dto.response;

public record PopupCrawlResultResponse(
        int total,
        int created,
        int updated,
        int skipped,
        int failed,
        int embeddingSuccess,
        int embeddingFailed
) {
}
