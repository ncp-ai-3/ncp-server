package com.ncp.team3.popup.controller.dto.response;

import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.domain.PopupCategory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record GetPopupDetailResponse(
        Long id,
        String imageUrl,
        String title,
        String description,
        String address,
        Double latitude,
        Double longitude,
        String naverPlaceId,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime openTime,
        LocalTime closeTime,
        String reservationUrl,
        List<CategoryResponse> categories
) {
    public static GetPopupDetailResponse from(Popup popup) {
        List<CategoryResponse> categories = popup.getPopupCategories().stream()
                .map(PopupCategory::getCategory)
                .map(CategoryResponse::from)
                .toList();

        return new GetPopupDetailResponse(
                popup.getId(),
                popup.getImageUrl(),
                popup.getTitle(),
                popup.getDescription(),
                popup.getAddress(),
                popup.getLatitude(),
                popup.getLongitude(),
                popup.getNaverPlaceId(),
                popup.getStartDate(),
                popup.getEndDate(),
                popup.getOpenTime(),
                popup.getCloseTime(),
                popup.getReservationUrl(),
                categories
        );
    }
}
