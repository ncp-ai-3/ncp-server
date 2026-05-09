package com.ncp.team3.popup.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

        @Size(max = 255, message = "메인 브랜드는 255자 이하여야 합니다.")
        String mainBrand,

        String hashtags,

        String description,

        @NotBlank(message = "주소는 필수입니다.")
        @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
        String address,

        @NotNull(message = "위도는 필수입니다.")
        Double latitude,

        @NotNull(message = "경도는 필수입니다.")
        Double longitude,

        @Positive(message = "팝업 ID는 1 이상입니다.")
        Long originId,

        @NotNull(message = "시작일은 필수입니다.")
        LocalDate startDate,

        @NotNull(message = "종료일은 필수입니다.")
        LocalDate endDate,

        LocalTime openTime,

        LocalTime closeTime,

        @Size(max = 255, message = "예약 URL은 255자 이하여야 합니다.")
        String reservationUrl,

        @Size(max = 50, message = "팝업 상태는 50자 이하여야 합니다.")
        String status,

        List<Long> categoryIds
) {
}
