package com.ncp.team3.popup.service;

import com.ncp.team3.popup.controller.dto.request.CreatePopupRequest;
import com.ncp.team3.popup.controller.dto.response.CreatePopupResponse;
import com.ncp.team3.popup.domain.Category;
import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.domain.PopupCategory;
import com.ncp.team3.popup.domain.exception.PopupDomainException;
import com.ncp.team3.popup.domain.exception.PopupErrorCode;
import com.ncp.team3.popup.port.CategoryRepository;
import com.ncp.team3.popup.port.PopupCategoryRepository;
import com.ncp.team3.popup.port.PopupRepository;
import com.ncp.team3.popup.usecase.command.CreatePopupUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PopupCommandService implements CreatePopupUseCase {
    private final PopupRepository popupRepository;
    private final CategoryRepository categoryRepository;
    private final PopupCategoryRepository popupCategoryRepository;

    @Override
    public CreatePopupResponse createPopup(CreatePopupRequest request) {
        Popup popup = Popup.create(
                request.imageUrl(),
                request.title(),
                request.mainBrand(),
                request.hashtags(),
                request.description(),
                request.address(),
                request.latitude(),
                request.longitude(),
                request.originId(),
                request.startDate(),
                request.endDate(),
                request.openTime(),
                request.closeTime(),
                request.reservationUrl(),
                request.status(),
                null
        );

        Popup savedPopup = popupRepository.save(popup);
        savePopupCategories(savedPopup, request.categoryIds());

        return CreatePopupResponse.from(savedPopup);
    }

    private void savePopupCategories(Popup popup, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        List<PopupCategory> popupCategories = categoryIds.stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new PopupDomainException(PopupErrorCode.CATEGORY_NOT_FOUND)))
                .map(category -> PopupCategory.create(popup, category))
                .toList();

        popupCategoryRepository.saveAll(popupCategories);
    }
}
