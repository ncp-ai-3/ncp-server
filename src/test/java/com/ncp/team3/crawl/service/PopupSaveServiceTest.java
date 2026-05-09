package com.ncp.team3.crawl.service;

import com.ncp.team3.crawl.infrastructure.dto.PopplyStoreDetail;
import com.ncp.team3.crawl.infrastructure.dto.PopplyStoreItem;
import com.ncp.team3.crawl.service.PopupSaveService.PopupSaveResult;
import com.ncp.team3.crawl.service.PopupSaveService.SaveStatus;
import com.ncp.team3.crawl.util.ContentHashGenerator;
import com.ncp.team3.crawl.util.WorkingTimeParser;
import com.ncp.team3.popup.domain.Category;
import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.domain.PopupCategory;
import com.ncp.team3.popup.port.CategoryRepository;
import com.ncp.team3.popup.port.PopupCategoryRepository;
import com.ncp.team3.popup.port.PopupRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PopupSaveServiceTest {
    private final PopupRepository popupRepository = mock(PopupRepository.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final PopupCategoryRepository popupCategoryRepository = mock(PopupCategoryRepository.class);
    private final WorkingTimeParser workingTimeParser = mock(WorkingTimeParser.class);
    private final PopupSaveService popupSaveService = new PopupSaveService(
            popupRepository,
            categoryRepository,
            popupCategoryRepository,
            workingTimeParser
    );

    @Test
    void 새로운_originId면_created를_반환한다() {
        PopplyStoreItem item = item("성수 팝업", 20, "https://pre-register");
        when(workingTimeParser.parse(item.workingTime()))
                .thenReturn(new WorkingTimeParser.WorkingTime(LocalTime.of(10, 0), LocalTime.of(20, 0)));
        when(popupRepository.findByOriginId(100L)).thenReturn(Optional.empty());
        when(popupRepository.save(any(Popup.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(categoryRepository.findByName("뷰티/헬스")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(popupCategoryRepository.save(any(PopupCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PopupSaveResult result = popupSaveService.saveOrUpdateOne(item);

        assertThat(result.status()).isEqualTo(SaveStatus.CREATED);
        assertThat(result.categoryNames()).containsExactly("뷰티/헬스");
        assertThat(result.popup().getReservationUrl()).isEqualTo("https://pre-register");
    }

    @Test
    void 기존_originId이고_contentHash가_같으면_skipped를_반환하고_저장하지_않는다() {
        PopplyStoreItem item = item("성수 팝업", 20, null);
        String hash = ContentHashGenerator.generate(
                "성수 팝업",
                "메인브랜드",
                "성수,팝업",
                "신제품 체험",
                List.of("뷰티/헬스"),
                "서울 성동구 성수동",
                "scheduled"
        );
        Popup existingPopup = popup(hash);

        when(workingTimeParser.parse(item.workingTime()))
                .thenReturn(new WorkingTimeParser.WorkingTime(LocalTime.of(10, 0), LocalTime.of(20, 0)));
        when(popupRepository.findByOriginId(100L)).thenReturn(Optional.of(existingPopup));

        PopupSaveResult result = popupSaveService.saveOrUpdateOne(item);

        assertThat(result.status()).isEqualTo(SaveStatus.SKIPPED);
        verify(popupRepository, never()).save(any(Popup.class));
        verify(popupCategoryRepository, never()).save(any(PopupCategory.class));
    }

    @Test
    void 기존_originId이고_contentHash가_다르면_updated를_반환한다() {
        PopplyStoreItem item = item("변경된 팝업", 20, null);
        Popup existingPopup = popup("old-hash");

        when(workingTimeParser.parse(item.workingTime()))
                .thenReturn(new WorkingTimeParser.WorkingTime(LocalTime.of(10, 0), LocalTime.of(20, 0)));
        when(popupRepository.findByOriginId(100L)).thenReturn(Optional.of(existingPopup));
        when(categoryRepository.findByName("뷰티/헬스")).thenReturn(Optional.of(Category.create("뷰티/헬스")));
        when(popupCategoryRepository.save(any(PopupCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PopupSaveResult result = popupSaveService.saveOrUpdateOne(item);

        assertThat(result.status()).isEqualTo(SaveStatus.UPDATED);
        assertThat(result.popup().getTitle()).isEqualTo("변경된 팝업");
    }

    @Test
    void preRegisterLink가_없으면_brandUrl을_reservationUrl로_사용한다() {
        PopplyStoreItem item = item("성수 팝업", 20, null);
        when(workingTimeParser.parse(item.workingTime()))
                .thenReturn(new WorkingTimeParser.WorkingTime(null, null));
        when(popupRepository.findByOriginId(100L)).thenReturn(Optional.empty());
        when(popupRepository.save(any(Popup.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(categoryRepository.findByName("뷰티/헬스")).thenReturn(Optional.of(Category.create("뷰티/헬스")));
        when(popupCategoryRepository.save(any(PopupCategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PopupSaveResult result = popupSaveService.saveOrUpdateOne(item);

        assertThat(result.popup().getReservationUrl()).isEqualTo("https://brand");
    }

    private PopplyStoreItem item(String title, Integer categoryId, String preRegisterLink) {
        return new PopplyStoreItem(
                100L,
                categoryId,
                "https://image",
                title,
                title,
                "메인브랜드",
                "서울 성동구",
                "서울 성동구",
                "성수동",
                37.1,
                127.1,
                "2026-05-01",
                "2026-05-31",
                "[]",
                preRegisterLink,
                "성수,팝업",
                "scheduled",
                new PopplyStoreDetail("<p>신제품 체험</p>", "https://brand")
        );
    }

    private Popup popup(String contentHash) {
        return Popup.create(
                "https://image",
                "성수 팝업",
                "메인브랜드",
                "성수,팝업",
                "신제품 체험",
                "서울 성동구 성수동",
                37.1,
                127.1,
                100L,
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31),
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                null,
                "scheduled",
                contentHash
        );
    }
}
