package com.ncp.team3.bookmark.usecase.command;

import com.ncp.team3.bookmark.controller.dto.request.CreateBookmarkRequest;
import com.ncp.team3.bookmark.controller.dto.response.CreateBookmarkResponse;

public interface CreateBookmarkUseCase {
    CreateBookmarkResponse createBookmark(CreateBookmarkRequest request);
}
