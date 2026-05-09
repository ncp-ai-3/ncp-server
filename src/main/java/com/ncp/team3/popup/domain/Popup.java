package com.ncp.team3.popup.domain;

import com.ncp.team3.common.BaseEntity;
import com.ncp.team3.popup.domain.exception.PopupDomainException;
import com.ncp.team3.popup.domain.exception.PopupErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "popup")
public class Popup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "main_brand", length = 255)
    private String mainBrand;

    @Column(name = "hashtags", columnDefinition = "TEXT")
    private String hashtags;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "origin_id", unique = true, nullable = false)
    private Long originId;

    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "reservation_url", length = 255)
    private String reservationUrl;

    @Column(name = "status", length = 50)
    private String status;

    @OneToMany(mappedBy = "popup")
    private List<PopupCategory> popupCategories = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Popup(String imageUrl, String title, String mainBrand, String hashtags, String description, String address,
                  Double latitude, Double longitude, Long originId, LocalDate startDate, LocalDate endDate,
                  LocalTime openTime, LocalTime closeTime, String reservationUrl, String status, String contentHash,
                  List<PopupCategory> popupCategories) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.mainBrand = mainBrand;
        this.hashtags = hashtags;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.originId = originId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.reservationUrl = reservationUrl;
        this.status = status;
        this.contentHash = contentHash;
        this.popupCategories = popupCategories;
    }

    public static Popup create(String imageUrl, String title, String mainBrand, String hashtags, String description,
                               String address, Double latitude, Double longitude, Long originId, LocalDate startDate,
                               LocalDate endDate, LocalTime openTime, LocalTime closeTime, String reservationUrl,
                               String status, String contentHash) {
        validateImageUrl(imageUrl);
        validateTitle(title);
        validateMainBrand(mainBrand);
        validateStatus(status);
        validateDescription(description);
        validateAddress(address);
        validateLocation(latitude, longitude);
        validateOriginId(originId);
        validateDate(startDate, endDate);
        validateTime(openTime, closeTime);
        validateReservationUrl(reservationUrl);

        return Popup.builder()
                .imageUrl(imageUrl)
                .title(title)
                .mainBrand(mainBrand)
                .hashtags(hashtags)
                .description(description)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .originId(originId)
                .startDate(startDate)
                .endDate(endDate)
                .openTime(openTime)
                .closeTime(closeTime)
                .reservationUrl(reservationUrl)
                .status(status)
                .contentHash(contentHash)
                .popupCategories(new ArrayList<>())
                .build();
    }

    public void updatePopup(String imageUrl, String title, String mainBrand, String hashtags, String description,
                            String address, Double latitude, Double longitude, LocalDate startDate, LocalDate endDate,
                            LocalTime openTime, LocalTime closeTime, String reservationUrl, String status,
                            String contentHash) {
        validateImageUrl(imageUrl);
        validateTitle(title);
        validateMainBrand(mainBrand);
        validateStatus(status);
        validateDescription(description);
        validateAddress(address);
        validateLocation(latitude, longitude);
        validateDate(startDate, endDate);
        validateTime(openTime, closeTime);
        validateReservationUrl(reservationUrl);

        this.imageUrl = imageUrl;
        this.title = title;
        this.mainBrand = mainBrand;
        this.hashtags = hashtags;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.reservationUrl = reservationUrl;
        this.status = status;
        this.contentHash = contentHash;
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

    private static void validateMainBrand(String mainBrand) {
        if (mainBrand != null && mainBrand.length() > 255) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_MAIN_BRAND);
        }
    }

    private static void validateStatus(String status) {
        if (status != null && status.length() > 50) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_STATUS);
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

    private static void validateOriginId(Long originId) {
        if (originId == null || originId <= 0) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_ORIGIN_ID);
        }
    }

    private static void validateDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_DATE);
        }
    }

    private static void validateTime(LocalTime openTime, LocalTime closeTime) {
        if (openTime == null && closeTime == null) {
            return;
        }

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
