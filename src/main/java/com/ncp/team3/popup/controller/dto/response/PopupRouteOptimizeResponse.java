package com.ncp.team3.popup.controller.dto.response;

import java.util.List;

public record PopupRouteOptimizeResponse(
        List<OrderedPopupResponse> orderedPopups,
        RouteResponse route
) {
}
