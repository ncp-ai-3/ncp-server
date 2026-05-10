package com.ncp.team3.popup.controller.dto.response;

import java.util.List;

public record RouteResponse(
        long totalDistanceMeter,
        long totalDurationMinute,
        long totalDurationMs,
        List<RoutePointResponse> path
) {
    public static RouteResponse empty() {
        return new RouteResponse(0, 0, 0, List.of());
    }
}
