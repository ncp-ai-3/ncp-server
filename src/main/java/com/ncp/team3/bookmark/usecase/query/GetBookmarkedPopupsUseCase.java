package com.ncp.team3.bookmark.usecase.query;

import com.ncp.team3.bookmark.controller.dto.response.BookmarkedPopupResponse;

import java.util.List;

public interface GetBookmarkedPopupsUseCase {
    List<BookmarkedPopupResponse> getBookmarkedPopups(Long memberId);
}
