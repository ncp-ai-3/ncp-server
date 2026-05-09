package com.ncp.team3.crawl.service;

import com.ncp.team3.crawl.domain.PopplyCategory;
import com.ncp.team3.crawl.infrastructure.dto.PopplyStoreDetail;
import com.ncp.team3.crawl.infrastructure.dto.PopplyStoreItem;
import com.ncp.team3.crawl.util.ContentHashGenerator;
import com.ncp.team3.crawl.util.HtmlTextExtractor;
import com.ncp.team3.crawl.util.WorkingTimeParser;
import com.ncp.team3.popup.domain.Category;
import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.domain.PopupCategory;
import com.ncp.team3.popup.port.CategoryRepository;
import com.ncp.team3.popup.port.PopupCategoryRepository;
import com.ncp.team3.popup.port.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopupSaveService {
    private final PopupRepository popupRepository;
    private final CategoryRepository categoryRepository;
    private final PopupCategoryRepository popupCategoryRepository;
    private final WorkingTimeParser workingTimeParser;

    @Transactional(readOnly = true)
    public PopupSaveContext prepareContext(List<PopplyStoreItem> items) {
        List<Long> originIds = items.stream()
                .map(PopplyStoreItem::storeId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<String> categoryNames = items.stream()
                .map(PopplyStoreItem::categoryId)
                .map(PopplyCategory::nameOf)
                .distinct()
                .toList();

        Map<Long, Popup> popupsByOriginId = originIds.isEmpty()
                ? new HashMap<>()
                : popupRepository.findAllByOriginIdIn(originIds).stream()
                .collect(Collectors.toMap(Popup::getOriginId, popup -> popup, (left, right) -> left, HashMap::new));

        Map<String, Category> categoriesByName = categoryNames.isEmpty()
                ? new HashMap<>()
                : categoryRepository.findAllByNameIn(categoryNames).stream()
                .collect(Collectors.toMap(Category::getName, category -> category, (left, right) -> left, HashMap::new));

        return new PopupSaveContext(popupsByOriginId, categoriesByName);
    }

    @Transactional
    public PopupSaveResult saveOrUpdateOne(PopplyStoreItem item) {
        return saveOrUpdateOne(item, null);
    }

    @Transactional
    public PopupSaveResult saveOrUpdateOne(PopplyStoreItem item, PopupSaveContext context) {
        PopupMappedData mappedData = map(item);
        Popup existingPopup = findExistingPopup(mappedData.originId(), context);

        if (existingPopup != null && mappedData.contentHash().equals(existingPopup.getContentHash())) {
            return PopupSaveResult.skipped(existingPopup, mappedData.categoryNames());
        }

        Popup popup = existingPopup == null
                ? createPopup(mappedData)
                : updatePopup(existingPopup, mappedData, context != null);

        if (context != null) {
            context.popupsByOriginId().put(mappedData.originId(), popup);
        }

        Category category = findOrCreateCategory(mappedData.categoryName(), context);

        popupCategoryRepository.deleteByPopupId(popup.getId());
        popupCategoryRepository.save(PopupCategory.create(popup, category));

        if (existingPopup == null) {
            return PopupSaveResult.created(popup, mappedData.categoryNames());
        }

        return PopupSaveResult.updated(popup, mappedData.categoryNames());
    }

    private Popup findExistingPopup(Long originId, PopupSaveContext context) {
        if (context != null) {
            return context.popupsByOriginId().get(originId);
        }

        return popupRepository.findByOriginId(originId).orElse(null);
    }

    private Category findOrCreateCategory(String categoryName, PopupSaveContext context) {
        if (context != null) {
            Category category = context.categoriesByName().get(categoryName);
            if (category == null) {
                category = categoryRepository.save(Category.create(categoryName));
                context.categoriesByName().put(categoryName, category);
            }
            return category;
        }

        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(Category.create(categoryName)));

        if (context != null) {
            context.categoriesByName().put(categoryName, category);
        }

        return category;
    }

    private Popup createPopup(PopupMappedData mappedData) {
        Popup popup = Popup.create(
                mappedData.imageUrl(),
                mappedData.title(),
                mappedData.mainBrand(),
                mappedData.hashtags(),
                mappedData.description(),
                mappedData.address(),
                mappedData.latitude(),
                mappedData.longitude(),
                mappedData.originId(),
                mappedData.startDate(),
                mappedData.endDate(),
                mappedData.openTime(),
                mappedData.closeTime(),
                mappedData.reservationUrl(),
                mappedData.status(),
                mappedData.contentHash()
        );

        return popupRepository.save(popup);
    }

    private Popup updatePopup(Popup popup, PopupMappedData mappedData, boolean mergeRequired) {
        popup.updatePopup(
                mappedData.imageUrl(),
                mappedData.title(),
                mappedData.mainBrand(),
                mappedData.hashtags(),
                mappedData.description(),
                mappedData.address(),
                mappedData.latitude(),
                mappedData.longitude(),
                mappedData.startDate(),
                mappedData.endDate(),
                mappedData.openTime(),
                mappedData.closeTime(),
                mappedData.reservationUrl(),
                mappedData.status(),
                mappedData.contentHash()
        );

        return mergeRequired ? popupRepository.save(popup) : popup;
    }

    private PopupMappedData map(PopplyStoreItem item) {
        String categoryName = PopplyCategory.nameOf(item.categoryId());
        List<String> categoryNames = List.of(categoryName);
        String title = titleOf(item);
        String mainBrand = blankToNull(item.mainBrand());
        String hashtags = blankToNull(item.hashtag());
        String status = blankToNull(item.status());
        String description = descriptionOf(item.storeDetail());
        String address = addressOf(item.address(), item.detailAddress());
        WorkingTimeParser.WorkingTime workingTime = workingTimeParser.parse(item.workingTime());
        String contentHash = ContentHashGenerator.generate(title, mainBrand, hashtags, description, categoryNames, address, status);

        return new PopupMappedData(
                item.storeId(),
                item.thumbnails(),
                title,
                mainBrand,
                hashtags,
                description,
                address,
                item.latitude(),
                item.longitude(),
                LocalDate.parse(item.startDate()),
                LocalDate.parse(item.endDate()),
                workingTime.openTime(),
                workingTime.closeTime(),
                reservationUrlOf(item),
                status,
                categoryName,
                categoryNames,
                contentHash
        );
    }

    private String titleOf(PopplyStoreItem item) {
        if (item.title() != null && !item.title().isBlank()) {
            return item.title();
        }

        return item.name();
    }

    private String descriptionOf(PopplyStoreDetail storeDetail) {
        return storeDetail == null ? null : HtmlTextExtractor.toPlainText(storeDetail.contents());
    }

    private String addressOf(String address, String detailAddress) {
        if (detailAddress == null || detailAddress.isBlank()) {
            return address;
        }

        return address + " " + detailAddress;
    }

    private String reservationUrlOf(PopplyStoreItem item) {
        if (item.preRegisterLink() != null && !item.preRegisterLink().isBlank()) {
            return item.preRegisterLink();
        }

        PopplyStoreDetail storeDetail = item.storeDetail();
        return storeDetail == null ? null : blankToNull(storeDetail.brandUrl());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private record PopupMappedData(
            Long originId,
            String imageUrl,
            String title,
            String mainBrand,
            String hashtags,
            String description,
            String address,
            Double latitude,
            Double longitude,
            LocalDate startDate,
            LocalDate endDate,
            java.time.LocalTime openTime,
            java.time.LocalTime closeTime,
            String reservationUrl,
            String status,
            String categoryName,
            List<String> categoryNames,
            String contentHash
    ) {
    }

    public record PopupSaveResult(
            Popup popup,
            SaveStatus status,
            List<String> categoryNames
    ) {
        public static PopupSaveResult created(Popup popup, List<String> categoryNames) {
            return new PopupSaveResult(popup, SaveStatus.CREATED, categoryNames);
        }

        public static PopupSaveResult updated(Popup popup, List<String> categoryNames) {
            return new PopupSaveResult(popup, SaveStatus.UPDATED, categoryNames);
        }

        public static PopupSaveResult skipped(Popup popup, List<String> categoryNames) {
            return new PopupSaveResult(popup, SaveStatus.SKIPPED, categoryNames);
        }
    }

    public enum SaveStatus {
        CREATED,
        UPDATED,
        SKIPPED
    }

    public record PopupSaveContext(
            Map<Long, Popup> popupsByOriginId,
            Map<String, Category> categoriesByName
    ) {
    }
}
