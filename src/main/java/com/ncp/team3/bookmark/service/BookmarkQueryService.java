package com.ncp.team3.bookmark.service;

import com.ncp.team3.bookmark.controller.dto.response.BookmarkedPopupResponse;
import com.ncp.team3.bookmark.port.BookmarkRepository;
import com.ncp.team3.bookmark.usecase.query.GetBookmarkedPopupsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkQueryService implements GetBookmarkedPopupsUseCase {
    private final BookmarkRepository bookmarkRepository;

    @Override
    public List<BookmarkedPopupResponse> getBookmarkedPopups(Long memberId) {
        return bookmarkRepository.findAllByMemberIdWithPopup(memberId).stream()
                .map(BookmarkedPopupResponse::from)
                .toList();
    }
}
