package com.ncp.team3.bookmark.controller;

import com.ncp.team3.bookmark.controller.dto.request.CreateBookmarkRequest;
import com.ncp.team3.bookmark.controller.dto.response.CreateBookmarkResponse;
import com.ncp.team3.bookmark.usecase.command.CreateBookmarkUseCase;
import com.ncp.team3.bookmark.usecase.command.DeleteBookmarkUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
@Tag(name = "북마크")
public class BookmarkController {
    private final CreateBookmarkUseCase createBookmarkUseCase;
    private final DeleteBookmarkUseCase deleteBookmarkUseCase;

    @PostMapping
    @Operation(summary = "북마크 등록", description = "북마크 등록합니다.")
    public CreateBookmarkResponse createBookmark(@Valid @RequestBody CreateBookmarkRequest request) {
        return createBookmarkUseCase.createBookmark(request);
    }

    @DeleteMapping("/{bookmarkId}")
    @Operation(summary = "북마크 삭제", description = "북마크를 삭제합니다.")
    public void deleteBookmark(@PathVariable Long bookmarkId) {
        deleteBookmarkUseCase.deleteBookmark(bookmarkId);
    }
}
