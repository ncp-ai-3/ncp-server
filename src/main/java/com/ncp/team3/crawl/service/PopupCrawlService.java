package com.ncp.team3.crawl.service;

import com.ncp.team3.crawl.controller.dto.response.PopupCrawlResultResponse;
import com.ncp.team3.crawl.infrastructure.dto.PopplyStoreItem;
import com.ncp.team3.crawl.service.PopupSaveService.PopupSaveResult;
import com.ncp.team3.crawl.service.PopupSaveService.PopupSaveContext;
import com.ncp.team3.crawl.service.PopupSaveService.SaveStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class PopupCrawlService {
    private final PopplyApiService popplyApiService;
    private final PopupSaveService popupSaveService;
    private final PopupEmbeddingService popupEmbeddingService;

    public PopupCrawlService(PopplyApiService popplyApiService,
                             PopupSaveService popupSaveService,
                             PopupEmbeddingService popupEmbeddingService) {
        this.popplyApiService = popplyApiService;
        this.popupSaveService = popupSaveService;
        this.popupEmbeddingService = popupEmbeddingService;
    }

    public PopupCrawlResultResponse crawl(LocalDate fromDate, LocalDate toDate) {
        List<PopplyStoreItem> items = popplyApiService.fetchStores(fromDate, toDate);
        PopupSaveContext saveContext = popupSaveService.prepareContext(items);

        CrawlCounter counter = new CrawlCounter(items.size());
        for (PopplyStoreItem item : items) {
            processItem(item, counter, saveContext);
        }

        return counter.toResponse();
    }

    private void processItem(PopplyStoreItem item, CrawlCounter counter, PopupSaveContext saveContext) {
        try {
            PopupSaveResult result = popupSaveService.saveOrUpdateOne(item, saveContext);
            counter.count(result.status());

            if (result.status() == SaveStatus.SKIPPED) {
                return;
            }

            try {
                boolean embedded = popupEmbeddingService.createOrUpdateEmbedding(result.popup(), result.categoryNames());
                if (embedded) {
                    counter.embeddingSuccess++;
                } else {
                    counter.embeddingFailed++;
                }
            } catch (Exception e) {
                counter.embeddingFailed++;
                log.warn("[POPUP EMBEDDING FAILED] originId={}, reason={}", item.storeId(), e.getMessage(), e);
            }
        } catch (Exception e) {
            counter.failed++;
            log.warn("[POPUP CRAWL ITEM FAILED] originId={}, reason={}", item.storeId(), e.getMessage());
        }
    }

    private static class CrawlCounter {
        private final int total;
        private int created;
        private int updated;
        private int skipped;
        private int failed;
        private int embeddingSuccess;
        private int embeddingFailed;

        private CrawlCounter(int total) {
            this.total = total;
        }

        private void count(SaveStatus status) {
            switch (status) {
                case CREATED -> created++;
                case UPDATED -> updated++;
                case SKIPPED -> skipped++;
            }
        }

        private PopupCrawlResultResponse toResponse() {
            return new PopupCrawlResultResponse(
                    total,
                    created,
                    updated,
                    skipped,
                    failed,
                    embeddingSuccess,
                    embeddingFailed
            );
        }
    }
}
