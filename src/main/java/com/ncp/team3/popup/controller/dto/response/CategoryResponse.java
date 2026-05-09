package com.ncp.team3.popup.controller.dto.response;

import com.ncp.team3.popup.domain.Category;

public record CategoryResponse(
        Long id,
        String name
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
