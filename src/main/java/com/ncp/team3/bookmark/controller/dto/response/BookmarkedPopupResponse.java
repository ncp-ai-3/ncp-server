package com.ncp.team3.bookmark.controller.dto.response;

import com.ncp.team3.bookmark.domain.Bookmark;
import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.domain.PopupCategory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record BookmarkedPopupResponse(
        Long bookmarkId,
        Long popupId,
        String imageUrl,
        String title,
        String address,
        String theme,
        LocalDate startDate,
        LocalDate endDate
) {
    public static BookmarkedPopupResponse from(Bookmark bookmark) {
        Popup popup = bookmark.getPopup();

        return new BookmarkedPopupResponse(
                bookmark.getId(),
                popup.getId(),
                popup.getImageUrl(),
                popup.getTitle(),
                popup.getAddress(),
                themeOf(popup),
                popup.getStartDate(),
                popup.getEndDate()
        );
    }

    private static String themeOf(Popup popup) {
        List<PopupCategory> popupCategories = popup.getPopupCategories();
        if (popupCategories == null || popupCategories.isEmpty()) {
            return null;
        }

        String theme = popupCategories.stream()
                .map(PopupCategory::getCategory)
                .filter(category -> category != null && category.getName() != null && !category.getName().isBlank())
                .map(category -> category.getName().trim())
                .distinct()
                .collect(Collectors.joining(", "));

        return theme.isBlank() ? null : theme;
    }
}
