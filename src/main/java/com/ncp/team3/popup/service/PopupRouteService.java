package com.ncp.team3.popup.service;

import com.ncp.team3.popup.controller.dto.request.PopupRouteOptimizeRequest;
import com.ncp.team3.popup.controller.dto.response.OrderedPopupResponse;
import com.ncp.team3.popup.controller.dto.response.PopupRouteOptimizeResponse;
import com.ncp.team3.popup.controller.dto.response.RouteResponse;
import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.domain.exception.PopupDomainException;
import com.ncp.team3.popup.domain.exception.PopupErrorCode;
import com.ncp.team3.popup.infrastructure.NaverDirectionsClient;
import com.ncp.team3.popup.port.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupRouteService {
    private static final double EARTH_RADIUS_METER = 6_371_000;
    private static final int MAX_DIRECTIONS_WAYPOINT_COUNT = 5;

    private final PopupRepository popupRepository;
    private final NaverDirectionsClient naverDirectionsClient;

    public PopupRouteOptimizeResponse optimizeRoute(PopupRouteOptimizeRequest request) {
        validateRequest(request);

        Popup startPopup = popupRepository.findById(request.startPopupId())
                .orElseThrow(() -> new PopupDomainException(PopupErrorCode.POPUP_NOT_FOUND));
        validateLocation(startPopup);

        LinkedHashSet<Long> targetPopupIds = new LinkedHashSet<>(request.targetPopupIds());
        targetPopupIds.remove(request.startPopupId());

        List<Popup> targetPopups = findTargetPopups(targetPopupIds);
        targetPopups.forEach(this::validateLocation);

        List<Popup> orderedPopups = optimizeByNearestNeighbor(startPopup, targetPopups);
        validateDirectionsWaypointCount(orderedPopups);
        RouteResponse route = orderedPopups.size() == 1
                ? RouteResponse.empty()
                : naverDirectionsClient.getDrivingRoute(orderedPopups);

        return new PopupRouteOptimizeResponse(toOrderedPopupResponses(orderedPopups), route);
    }

    private void validateRequest(PopupRouteOptimizeRequest request) {
        if (request.startPopupId() == null) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_ROUTE_REQUEST, "시작 팝업 ID는 필수입니다.");
        }

        if (request.targetPopupIds() == null) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_ROUTE_REQUEST, "방문할 팝업 ID 목록은 필수입니다.");
        }

        if (request.targetPopupIds().stream().anyMatch(id -> id == null)) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_ROUTE_REQUEST, "방문할 팝업 ID는 null일 수 없습니다.");
        }
    }

    private void validateDirectionsWaypointCount(List<Popup> orderedPopups) {
        int waypointCount = Math.max(orderedPopups.size() - 2, 0);
        if (waypointCount > MAX_DIRECTIONS_WAYPOINT_COUNT) {
            throw new PopupDomainException(
                    PopupErrorCode.INVALID_POPUP_ROUTE_REQUEST,
                    "네이버 Directions 5 API 경유지는 최대 5개까지 요청할 수 있습니다."
            );
        }
    }

    private List<Popup> findTargetPopups(LinkedHashSet<Long> targetPopupIds) {
        if (targetPopupIds.isEmpty()) {
            return List.of();
        }

        List<Popup> popups = popupRepository.findAllById(targetPopupIds);
        if (popups.size() != targetPopupIds.size()) {
            throw new PopupDomainException(PopupErrorCode.POPUP_NOT_FOUND);
        }

        Map<Long, Popup> popupById = popups.stream()
                .collect(Collectors.toMap(Popup::getId, Function.identity()));

        return targetPopupIds.stream()
                .map(popupById::get)
                .toList();
    }

    private List<Popup> optimizeByNearestNeighbor(Popup startPopup, List<Popup> targetPopups) {
        List<Popup> orderedPopups = new ArrayList<>();
        orderedPopups.add(startPopup);

        List<Popup> remainingPopups = new ArrayList<>(targetPopups);
        Popup current = startPopup;

        while (!remainingPopups.isEmpty()) {
            Popup nearest = findNearestPopup(current, remainingPopups);
            orderedPopups.add(nearest);
            remainingPopups.remove(nearest);
            current = nearest;
        }

        return orderedPopups;
    }

    private Popup findNearestPopup(Popup current, List<Popup> candidates) {
        Popup nearest = candidates.get(0);
        double nearestDistance = haversineDistanceMeter(current, nearest);

        for (int i = 1; i < candidates.size(); i++) {
            Popup candidate = candidates.get(i);
            double distance = haversineDistanceMeter(current, candidate);
            if (distance < nearestDistance) {
                nearest = candidate;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    private double haversineDistanceMeter(Popup from, Popup to) {
        double fromLatitude = Math.toRadians(from.getLatitude());
        double toLatitude = Math.toRadians(to.getLatitude());
        double latitudeDelta = Math.toRadians(to.getLatitude() - from.getLatitude());
        double longitudeDelta = Math.toRadians(to.getLongitude() - from.getLongitude());

        double a = Math.sin(latitudeDelta / 2) * Math.sin(latitudeDelta / 2)
                + Math.cos(fromLatitude) * Math.cos(toLatitude)
                * Math.sin(longitudeDelta / 2) * Math.sin(longitudeDelta / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METER * c;
    }

    private void validateLocation(Popup popup) {
        Double latitude = popup.getLatitude();
        Double longitude = popup.getLongitude();

        if (latitude == null || longitude == null) {
            throw new PopupDomainException(
                    PopupErrorCode.INVALID_POPUP_LOCATION,
                    "팝업 위치 정보가 없습니다. popupId=" + popup.getId()
            );
        }

        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new PopupDomainException(
                    PopupErrorCode.INVALID_POPUP_LOCATION,
                    "팝업 위치 정보 범위가 유효하지 않습니다. popupId=" + popup.getId()
            );
        }
    }

    private List<OrderedPopupResponse> toOrderedPopupResponses(List<Popup> orderedPopups) {
        List<OrderedPopupResponse> responses = new ArrayList<>();
        for (int i = 0; i < orderedPopups.size(); i++) {
            responses.add(OrderedPopupResponse.from(orderedPopups.get(i), i + 1));
        }
        return responses;
    }
}
