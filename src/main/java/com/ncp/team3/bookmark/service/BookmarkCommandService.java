package com.ncp.team3.bookmark.service;

import com.ncp.team3.bookmark.controller.dto.request.CreateBookmarkRequest;
import com.ncp.team3.bookmark.controller.dto.response.CreateBookmarkResponse;
import com.ncp.team3.bookmark.domain.Bookmark;
import com.ncp.team3.bookmark.domain.exception.BookmarkDomainException;
import com.ncp.team3.bookmark.domain.exception.BookmarkErrorCode;
import com.ncp.team3.bookmark.port.BookmarkRepository;
import com.ncp.team3.bookmark.usecase.command.CreateBookmarkUseCase;
import com.ncp.team3.bookmark.usecase.command.DeleteBookmarkUseCase;
import com.ncp.team3.member.domain.Member;
import com.ncp.team3.member.domain.exception.MemberDomainException;
import com.ncp.team3.member.domain.exception.MemberErrorCode;
import com.ncp.team3.member.port.MemberRepository;
import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.domain.exception.PopupDomainException;
import com.ncp.team3.popup.domain.exception.PopupErrorCode;
import com.ncp.team3.popup.port.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkCommandService implements CreateBookmarkUseCase, DeleteBookmarkUseCase {
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final PopupRepository popupRepository;

    @Override
    public CreateBookmarkResponse createBookmark(CreateBookmarkRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new MemberDomainException(MemberErrorCode.MEMBER_NOT_FOUND));
        Popup popup = popupRepository.findById(request.popupId())
                .orElseThrow(() -> new PopupDomainException(PopupErrorCode.POPUP_NOT_FOUND));

        if (bookmarkRepository.existsByMemberIdAndPopupId(member.getId(), popup.getId())) {
            throw new BookmarkDomainException(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        Bookmark bookmark = Bookmark.create(member, popup);
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        return CreateBookmarkResponse.from(savedBookmark);
    }

    @Override
    public void deleteBookmark(Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BookmarkDomainException(BookmarkErrorCode.BOOKMARK_NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }
}
