package com.ncp.team3.popup.controller;

import com.ncp.team3.popup.controller.dto.request.PopupRouteOptimizeRequest;
import com.ncp.team3.popup.controller.dto.response.PopupRouteOptimizeResponse;
import com.ncp.team3.popup.service.PopupRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/popups/routes")
@RequiredArgsConstructor
@Tag(name = "팝업 경로")
public class PopupRouteController {
    private final PopupRouteService popupRouteService;

    @PostMapping("/optimize")
    @Operation(summary = "팝업 방문 경로 최적화", description = "시작 팝업 기준으로 방문 순서를 최적화하고 네이버 Directions 경로를 반환합니다.")
    public PopupRouteOptimizeResponse optimizeRoute(@Valid @RequestBody PopupRouteOptimizeRequest request) {
        return popupRouteService.optimizeRoute(request);
    }
}
