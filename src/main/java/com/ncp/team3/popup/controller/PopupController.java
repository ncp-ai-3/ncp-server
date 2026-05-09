package com.ncp.team3.popup.controller;

import com.ncp.team3.popup.controller.dto.request.CreatePopupRequest;
import com.ncp.team3.popup.controller.dto.response.CreatePopupResponse;
import com.ncp.team3.popup.controller.dto.response.GetPopupDetailResponse;
import com.ncp.team3.popup.usecase.command.CreatePopupUseCase;
import com.ncp.team3.popup.usecase.query.GetPopupDetailUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/popups")
@RequiredArgsConstructor
@Tag(name = "팝업")
public class PopupController {
    private final CreatePopupUseCase createPopupUseCase;
    private final GetPopupDetailUseCase getPopupDetailUseCase;

    @PostMapping
    @Operation(summary = "팝업 생성", description = "팝업 정보를 생성합니다.")
    public CreatePopupResponse createPopup(@Valid @RequestBody CreatePopupRequest request) {
        return createPopupUseCase.createPopup(request);
    }

    @GetMapping("/{popupId}")
    @Operation(summary = "팝업 상세 조회", description = "팝업 상세 정보를 조회합니다.")
    public GetPopupDetailResponse getPopupDetail(@PathVariable Long popupId) {
        return getPopupDetailUseCase.getPopupDetail(popupId);
    }
}
