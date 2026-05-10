package com.ncp.team3.crawl.controller;

import com.ncp.team3.crawl.controller.dto.response.PopupCrawlResultResponse;
import com.ncp.team3.crawl.service.PopupCrawlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/popups/crawl")
@RequiredArgsConstructor
@Tag(name = "팝업 크롤링")
public class PopupCrawlController {
    private final PopupCrawlService popupCrawlService;

    @PostMapping
    public PopupCrawlResultResponse crawl(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return popupCrawlService.crawl(fromDate, toDate);
    }
}
