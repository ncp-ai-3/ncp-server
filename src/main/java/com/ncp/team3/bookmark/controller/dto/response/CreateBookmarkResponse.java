package com.ncp.team3.bookmark.controller.dto.response;

import com.ncp.team3.bookmark.domain.Bookmark;

public record CreateBookmarkResponse(
        Long id,
        Long memberId,
        Long popupId
) {
    public static CreateBookmarkResponse from(Bookmark bookmark) {
        return new CreateBookmarkResponse(
                bookmark.getId(),
                bookmark.getMember().getId(),
                bookmark.getPopup().getId()
        );
    }
}
