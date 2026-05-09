package com.ncp.team3.popup.domain;

import com.ncp.team3.common.BaseEntity;
import com.ncp.team3.popup.domain.exception.PopupDomainException;
import com.ncp.team3.popup.domain.exception.PopupErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "popup")
public class Popup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id")
    private Long popupId;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "naver_place_id", length = 255)
    private String naverPlaceId;

    @Column(name = "strat_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "reservationi_url", length = 255)
    private String reservationUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private Popup(String imageUrl, String title, String description, String address, Double latitude, Double longitude,
                  String naverPlaceId, LocalDate startDate, LocalDate endDate, LocalTime openTime, LocalTime closeTime,
                  String reservationUrl) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.naverPlaceId = naverPlaceId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.reservationUrl = reservationUrl;
    }

    public static Popup create(String imageUrl, String title, String description, String address, Double latitude,
                               Double longitude, String naverPlaceId, LocalDate startDate, LocalDate endDate,
                               LocalTime openTime, LocalTime closeTime, String reservationUrl) {
        validateImageUrl(imageUrl);
        validateTitle(title);
        validateDescription(description);
        validateAddress(address);
        validateLocation(latitude, longitude);
        validateNaverPlaceId(naverPlaceId);
        validateDate(startDate, endDate);
        validateTime(openTime, closeTime);
        validateReservationUrl(reservationUrl);

        return Popup.builder()
                .imageUrl(imageUrl)
                .title(title)
                .description(description)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .naverPlaceId(naverPlaceId)
                .startDate(startDate)
                .endDate(endDate)
                .openTime(openTime)
                .closeTime(closeTime)
                .reservationUrl(reservationUrl)
                .build();
    }

    public void updatePopup(String imageUrl, String title, String description, String address, Double latitude,
                            Double longitude, String naverPlaceId, LocalDate startDate, LocalDate endDate,
                            LocalTime openTime, LocalTime closeTime, String reservationUrl) {
        validateImageUrl(imageUrl);
        validateTitle(title);
        validateDescription(description);
        validateAddress(address);
        validateLocation(latitude, longitude);
        validateNaverPlaceId(naverPlaceId);
        validateDate(startDate, endDate);
        validateTime(openTime, closeTime);
        validateReservationUrl(reservationUrl);

        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.naverPlaceId = naverPlaceId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.reservationUrl = reservationUrl;
    }

    private static void validateImageUrl(String imageUrl) {
        if (imageUrl != null && imageUrl.isBlank()) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_IMAGE_URL);
        }
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank() || title.length() > 255) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_TITLE);
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.isBlank()) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_DESCRIPTION);
        }
    }

    private static void validateAddress(String address) {
        if (address == null || address.isBlank() || address.length() > 255) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_ADDRESS);
        }
    }

    private static void validateLocation(Double latitude, Double longitude) {
        if (latitude == null || longitude == null || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_LOCATION);
        }
    }

    private static void validateNaverPlaceId(String naverPlaceId) {
        if (naverPlaceId != null && (naverPlaceId.isBlank() || naverPlaceId.length() > 255)) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_NAVER_PLACE_ID);
        }
    }

    private static void validateDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_DATE);
        }
    }

    private static void validateTime(LocalTime openTime, LocalTime closeTime) {
        if (openTime == null || closeTime == null || !openTime.isBefore(closeTime)) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_TIME);
        }
    }

    private static void validateReservationUrl(String reservationUrl) {
        if (reservationUrl != null && reservationUrl.isBlank()) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_RESERVATION_URL);
        }
    }
}
