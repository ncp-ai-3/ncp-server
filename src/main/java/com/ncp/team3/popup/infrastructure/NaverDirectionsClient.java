package com.ncp.team3.popup.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncp.team3.global.logging.LogMaskingUtils;
import com.ncp.team3.popup.controller.dto.response.RoutePointResponse;
import com.ncp.team3.popup.controller.dto.response.RouteResponse;
import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.domain.exception.PopupDomainException;
import com.ncp.team3.popup.domain.exception.PopupErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NaverDirectionsClient {
    private static final String OPTION_TRAOPTIMAL = "traoptimal";
    private static final int MAX_WAYPOINT_COUNT = 5;

    private final RestTemplate restTemplate;
    private final String directionsUrl;
    private final String clientId;
    private final String clientSecret;
    private final ObjectMapper objectMapper;

    public NaverDirectionsClient(RestTemplateBuilder restTemplateBuilder,
                                 ObjectMapper objectMapper,
                                 @Value("${naver.maps.directions-url}") String directionsUrl,
                                 @Value("${naver.maps.client-id}") String clientId,
                                 @Value("${naver.maps.client-secret}") String clientSecret) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.directionsUrl = directionsUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.objectMapper = objectMapper;
    }

    public RouteResponse getDrivingRoute(List<Popup> orderedPopups) {
        List<Popup> uniqueOrderedPopups = removeDuplicateCoordinates(orderedPopups);
        int removedDuplicateCount = orderedPopups.size() - uniqueOrderedPopups.size();
        if (removedDuplicateCount > 0) {
            log.info("[ROUTE OPTIMIZE] removed duplicated coordinate from naver request count={}", removedDuplicateCount);
        }

        if (uniqueOrderedPopups.size() < 2) {
            return RouteResponse.empty();
        }
        validateWaypointCount(uniqueOrderedPopups);

        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new PopupDomainException(PopupErrorCode.POPUP_ROUTE_FAILED, "네이버 지도 API client-id/client-secret 설정이 필요합니다.");
        }

        URI uri = buildUri(uniqueOrderedPopups);
        log.info("[NAVER DIRECTIONS REQUEST] uri={}", uri);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-ncp-apigw-api-key-id", clientId);
        headers.set("x-ncp-apigw-api-key", clientSecret);

        long startTime = System.currentTimeMillis();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            long durationMs = System.currentTimeMillis() - startTime;
            String responseBody = response.getBody();
            JsonNode body = readBody(responseBody);
            log.info("[NAVER DIRECTIONS RESPONSE] status={} code={} message={} durationMs={} bodyPreview={}",
                    response.getStatusCode().value(), responseCode(body), responseMessage(body), durationMs,
                    LogMaskingUtils.bodyPreview(responseBody));
            validateDirectionsResponse(body, response.getStatusCode().value(), durationMs, responseBody);
            return toRouteResponse(body);
        } catch (PopupDomainException e) {
            throw e;
        } catch (RestClientResponseException e) {
            long durationMs = System.currentTimeMillis() - startTime;
            JsonNode body = readBody(e.getResponseBodyAsString());
            log.warn("[NAVER DIRECTIONS ERROR] status={} code={} message={} bodyPreview={} durationMs={}",
                    e.getStatusCode().value(), responseCode(body), responseMessage(body),
                    LogMaskingUtils.bodyPreview(e.getResponseBodyAsString()), durationMs);
            throw new PopupDomainException(PopupErrorCode.POPUP_ROUTE_FAILED);
        } catch (RestClientException e) {
            long durationMs = System.currentTimeMillis() - startTime;
            log.warn("[NAVER DIRECTIONS ERROR] status=unknown code=unknown message={} bodyPreview= durationMs={}",
                    LogMaskingUtils.maskSensitiveValues(e.getMessage()), durationMs);
            throw new PopupDomainException(PopupErrorCode.POPUP_ROUTE_FAILED);
        }
    }

    private List<Popup> removeDuplicateCoordinates(List<Popup> orderedPopups) {
        Map<String, Popup> uniquePopupsByCoordinate = new LinkedHashMap<>();
        for (Popup popup : orderedPopups) {
            uniquePopupsByCoordinate.putIfAbsent(coordinateKey(popup), popup);
        }
        return List.copyOf(uniquePopupsByCoordinate.values());
    }

    private void validateWaypointCount(List<Popup> orderedPopups) {
        int waypointCount = Math.max(orderedPopups.size() - 2, 0);
        if (waypointCount > MAX_WAYPOINT_COUNT) {
            throw new PopupDomainException(
                    PopupErrorCode.INVALID_POPUP_ROUTE_REQUEST,
                    "네이버 Directions 5 API 경유지는 최대 5개까지 요청할 수 있습니다."
            );
        }
    }

    private URI buildUri(List<Popup> orderedPopups) {
        Popup start = orderedPopups.get(0);
        Popup goal = orderedPopups.get(orderedPopups.size() - 1);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(directionsUrl)
                .queryParam("start", coordinate(start))
                .queryParam("goal", coordinate(goal))
                .queryParam("option", OPTION_TRAOPTIMAL);

        if (orderedPopups.size() > 2) {
            String waypoints = orderedPopups.subList(1, orderedPopups.size() - 1).stream()
                    .map(this::coordinate)
                    .collect(Collectors.joining("|"));
            builder.queryParam("waypoints", waypoints);
        }

        return builder.build().encode().toUri();
    }

    private String coordinate(Popup popup) {
        return popup.getLongitude() + "," + popup.getLatitude();
    }

    private String coordinateKey(Popup popup) {
        return String.format(Locale.US, "%.7f,%.7f", popup.getLongitude(), popup.getLatitude());
    }

    private void validateDirectionsResponse(JsonNode body, int status, long durationMs, String responseBody) {
        if (body == null || body.path("code").asInt(-1) != 0) {
            log.warn("[NAVER DIRECTIONS ERROR] status={} code={} message={} bodyPreview={} durationMs={}",
                    status, responseCode(body), responseMessage(body), LogMaskingUtils.bodyPreview(responseBody), durationMs);
            throw new PopupDomainException(PopupErrorCode.POPUP_ROUTE_FAILED);
        }

        JsonNode traoptimal = body.path("route").path("traoptimal");
        if (!traoptimal.isArray() || traoptimal.isEmpty()) {
            log.warn("[NAVER DIRECTIONS ERROR] status={} code={} message={} bodyPreview={} durationMs={}",
                    status, responseCode(body), "traoptimal route is empty", LogMaskingUtils.bodyPreview(responseBody), durationMs);
            throw new PopupDomainException(PopupErrorCode.POPUP_ROUTE_FAILED);
        }
    }

    private JsonNode readBody(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readTree(responseBody);
        } catch (Exception e) {
            return null;
        }
    }

    private String responseCode(JsonNode body) {
        if (body == null) {
            return "unknown";
        }
        if (body.has("code")) {
            return body.path("code").asText();
        }
        if (body.path("error").has("errorCode")) {
            return body.path("error").path("errorCode").asText();
        }
        return "unknown";
    }

    private String responseMessage(JsonNode body) {
        if (body == null) {
            return "unknown";
        }
        if (body.has("message")) {
            return LogMaskingUtils.maskSensitiveValues(body.path("message").asText());
        }
        if (body.path("error").has("message")) {
            return LogMaskingUtils.maskSensitiveValues(body.path("error").path("message").asText());
        }
        return "unknown";
    }

    private RouteResponse toRouteResponse(JsonNode body) {
        JsonNode route = body.path("route").path("traoptimal").get(0);
        JsonNode summary = route.path("summary");

        long distance = summary.path("distance").asLong(0);
        long durationMs = summary.path("duration").asLong(0);
        long durationMinute = (long) Math.ceil(durationMs / 60000.0);

        List<RoutePointResponse> path = new ArrayList<>();
        for (JsonNode point : route.path("path")) {
            if (point.isArray() && point.size() >= 2) {
                double longitude = point.get(0).asDouble();
                double latitude = point.get(1).asDouble();
                path.add(new RoutePointResponse(latitude, longitude));
            }
        }

        return new RouteResponse(distance, durationMinute, durationMs, path);
    }
}
