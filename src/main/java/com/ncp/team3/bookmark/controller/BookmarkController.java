package com.ncp.team3.bookmark.controller;

import com.ncp.team3.bookmark.controller.dto.request.CreateBookmarkRequest;
import com.ncp.team3.bookmark.controller.dto.response.BookmarkedPopupResponse;
import com.ncp.team3.bookmark.controller.dto.response.CreateBookmarkResponse;
import com.ncp.team3.bookmark.usecase.command.CreateBookmarkUseCase;
import com.ncp.team3.bookmark.usecase.command.DeleteBookmarkUseCase;
import com.ncp.team3.bookmark.usecase.query.GetBookmarkedPopupsUseCase;
import com.ncp.team3.global.security.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
@Tag(name = "북마크")
public class BookmarkController {
    private final CreateBookmarkUseCase createBookmarkUseCase;
    private final DeleteBookmarkUseCase deleteBookmarkUseCase;
    private final GetBookmarkedPopupsUseCase getBookmarkedPopupsUseCase;

    @GetMapping
    @Operation(summary = "북마크 목록 조회", description = "내가 등록한 북마크 팝업 목록을 조회합니다.")
    public List<BookmarkedPopupResponse> getBookmarkedPopups(@AuthenticationPrincipal MemberPrincipal principal) {
        return getBookmarkedPopupsUseCase.getBookmarkedPopups(principal.getMemberId());
    }

    @PostMapping
    @Operation(summary = "북마크 등록", description = "북마크 등록합니다.")
    public CreateBookmarkResponse createBookmark(@AuthenticationPrincipal MemberPrincipal principal,
                                                 @Valid @RequestBody CreateBookmarkRequest request) {
        return createBookmarkUseCase.createBookmark(principal.getMemberId(), request);
    }

    @DeleteMapping("/{bookmarkId}")
    @Operation(summary = "북마크 삭제", description = "북마크를 삭제합니다.")
    public void deleteBookmark(@AuthenticationPrincipal MemberPrincipal principal,
                               @PathVariable Long bookmarkId) {
        deleteBookmarkUseCase.deleteBookmark(principal.getMemberId(), bookmarkId);
    }
}
