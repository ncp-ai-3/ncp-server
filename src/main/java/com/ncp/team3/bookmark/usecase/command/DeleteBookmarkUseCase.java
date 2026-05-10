package com.ncp.team3.bookmark.usecase.command;

public interface DeleteBookmarkUseCase {
    void deleteBookmark(Long memberId, Long bookmarkId);
}
