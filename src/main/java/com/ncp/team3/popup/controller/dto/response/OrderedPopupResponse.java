package com.ncp.team3.popup.controller.dto.response;

import com.ncp.team3.popup.domain.Popup;

import java.time.LocalDate;
import java.time.LocalTime;

public record OrderedPopupResponse(
        Long popupId,
        String title,
        String imageUrl,
        String address,
        Double latitude,
        Double longitude,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime openTime,
        LocalTime closeTime,
        String reservationUrl,
        String status,
        int order
) {
    public static OrderedPopupResponse from(Popup popup, int order) {
        return new OrderedPopupResponse(
                popup.getId(),
                popup.getTitle(),
                popup.getImageUrl(),
                popup.getAddress(),
                popup.getLatitude(),
                popup.getLongitude(),
                popup.getStartDate(),
                popup.getEndDate(),
                popup.getOpenTime(),
                popup.getCloseTime(),
                popup.getReservationUrl(),
                popup.getStatus(),
                order
        );
    }
}
