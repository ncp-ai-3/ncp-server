package com.ncp.team3.crawl.service;

import com.ncp.team3.crawl.infrastructure.PopplyApiClient;
import com.ncp.team3.crawl.infrastructure.dto.PopplyStoreItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopplyApiService {
    private final PopplyApiClient popplyApiClient;

    public List<PopplyStoreItem> fetchStores(LocalDate fromDate, LocalDate toDate) {
        return popplyApiClient.fetchStores(fromDate, toDate);
    }
}
