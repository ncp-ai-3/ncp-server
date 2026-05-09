package com.ncp.team3.popup.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreatePopupRequest(
        @Size(max = 255, message = "이미지 URL은 255자 이하여야 합니다.")
        String imageUrl,

        @NotBlank(message = "팝업 제목은 필수입니다.")
        @Size(max = 255, message = "팝업 제목은 255자 이하여야 합니다.")
        String title,

        String description,

        @NotBlank(message = "주소는 필수입니다.")
        @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
        String address,

        @NotNull(message = "위도는 필수입니다.")
        Double latitude,

        @NotNull(message = "경도는 필수입니다.")
        Double longitude,

        @Size(max = 255, message = "네이버 장소 ID는 255자 이하여야 합니다.")
        String naverPlaceId,

        @NotNull(message = "시작일은 필수입니다.")
        LocalDate startDate,

        @NotNull(message = "종료일은 필수입니다.")
        LocalDate endDate,

        @NotNull(message = "오픈 시간은 필수입니다.")
        LocalTime openTime,

        @NotNull(message = "마감 시간은 필수입니다.")
        LocalTime closeTime,

        @Size(max = 255, message = "예약 URL은 255자 이하여야 합니다.")
        String reservationUrl,

        List<Long> categoryIds
) {
}
