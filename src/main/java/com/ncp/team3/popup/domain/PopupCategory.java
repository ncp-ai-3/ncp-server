package com.ncp.team3.popup.domain;

import com.ncp.team3.popup.domain.exception.PopupDomainException;
import com.ncp.team3.popup.domain.exception.PopupErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "popup_category")
public class PopupCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Popup popup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cateroty_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    @Builder(access = AccessLevel.PRIVATE)
    private PopupCategory(Popup popup, Category category) {
        this.popup = popup;
        this.category = category;
    }

    public static PopupCategory create(Popup popup, Category category) {
        validatePopup(popup);
        validateCategory(category);

        return PopupCategory.builder()
                .popup(popup)
                .category(category)
                .build();
    }

    private static void validatePopup(Popup popup) {
        if (popup == null) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_CATEGORY_POPUP);
        }
    }

    private static void validateCategory(Category category) {
        if (category == null) {
            throw new PopupDomainException(PopupErrorCode.INVALID_POPUP_CATEGORY_CATEGORY);
        }
    }
}
