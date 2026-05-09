package com.ncp.team3.crawl.infrastructure;

import com.ncp.team3.crawl.infrastructure.dto.PopplyStoreItem;
import com.ncp.team3.crawl.infrastructure.dto.PopplyStoreResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PopplyApiClient {
    private static final List<String> SEOUL_DISTRICTS = List.of(
            "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구",
            "노원구", "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구",
            "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"
    );

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String storePath;
    private final String origin;
    private final String referer;

    public PopplyApiClient(RestTemplateBuilder restTemplateBuilder,
                           @Value("${popply.base-url}") String baseUrl,
                           @Value("${popply.store-path}") String storePath,
                           @Value("${popply.origin}") String origin,
                           @Value("${popply.referer}") String referer,
                           @Value("${popply.timeout-seconds}") long timeoutSeconds) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(timeoutSeconds))
                .setReadTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        this.baseUrl = baseUrl;
        this.storePath = storePath;
        this.origin = origin;
        this.referer = referer;
    }

    public List<PopplyStoreItem> fetchStores(LocalDate fromDate, LocalDate toDate) {
        Map<Long, PopplyStoreItem> storesById = new LinkedHashMap<>();

        for (String district : SEOUL_DISTRICTS) {
            fetchStoresByDistrict(fromDate, toDate, district).stream()
                    .filter(item -> item.storeId() != null)
                    .forEach(item -> storesById.putIfAbsent(item.storeId(), item));
        }

        log.info("[POPPLY API TOTAL] fromDate={}, toDate={}, uniqueCount={}", fromDate, toDate, storesById.size());
        return List.copyOf(storesById.values());
    }

    private List<PopplyStoreItem> fetchStoresByDistrict(LocalDate fromDate, LocalDate toDate, String district) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(storePath)
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .queryParam("address1", "서울")
                .queryParam("address2", district)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0");
        headers.set(HttpHeaders.ACCEPT, "application/json");
        headers.set(HttpHeaders.ORIGIN, origin);
        headers.set(HttpHeaders.REFERER, referer);

        try {
            ResponseEntity<PopplyStoreResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    PopplyStoreResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("[POPPLY API FAILED] status={}", response.getStatusCode());
                return List.of();
            }

            PopplyStoreResponse body = response.getBody();
            List<PopplyStoreItem> items = body == null || body.data() == null ? List.of() : body.data();
            log.info("[POPPLY API DISTRICT] district={}, count={}", district, items.size());
            return items;
        } catch (RestClientException e) {
            log.warn("[POPPLY API ERROR] district={}, reason={}", district, e.getMessage());
            return List.of();
        }
    }
}
