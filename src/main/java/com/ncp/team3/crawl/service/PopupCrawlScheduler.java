package com.ncp.team3.crawl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopupCrawlScheduler {
    private final PopupCrawlService popupCrawlService;

    @Scheduled(cron = "0 0 3 * * *")
    public void crawlDailyPopups() {
        LocalDate today = LocalDate.now();
        LocalDate fromDate = today.minusDays(1);
        LocalDate toDate = today.plusDays(30);

        log.info("[POPUP CRAWL SCHEDULED] fromDate={}, toDate={}", fromDate, toDate);
        popupCrawlService.crawl(fromDate, toDate);
    }
}
