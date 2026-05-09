package com.ncp.team3.popup.domain;

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

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 255)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<PopupCategory> popupCategories = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Category(String name, List<PopupCategory> popupCategories) {
        this.name = name;
        this.popupCategories = popupCategories;
    }

    public static Category create(String name) {
        validateName(name);

        return Category.builder()
                .name(name)
                .popupCategories(new ArrayList<>())
                .build();
    }

    public void updateName(String name) {
        validateName(name);

        this.name = name;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > 255) {
            throw new PopupDomainException(PopupErrorCode.INVALID_CATEGORY_NAME);
        }
    }
}
