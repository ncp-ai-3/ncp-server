package com.ncp.team3.bookmark.domain;

import com.ncp.team3.bookmark.domain.exception.BookmarkDomainException;
import com.ncp.team3.bookmark.domain.exception.BookmarkErrorCode;
import com.ncp.team3.common.BaseEntity;
import com.ncp.team3.member.domain.Member;
import com.ncp.team3.popup.domain.Popup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bookmark")
public class Bookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Popup popup;

    @Builder(access = AccessLevel.PRIVATE)
    private Bookmark(Member member, Popup popup) {
        this.member = member;
        this.popup = popup;
    }

    public static Bookmark create(Member member, Popup popup) {
        validateMember(member);
        validatePopup(popup);

        return Bookmark.builder()
                .member(member)
                .popup(popup)
                .build();
    }

    private static void validateMember(Member member) {
        if (member == null) {
            throw new BookmarkDomainException(BookmarkErrorCode.INVALID_BOOKMARK_MEMBER);
        }
    }

    private static void validatePopup(Popup popup) {
        if (popup == null) {
            throw new BookmarkDomainException(BookmarkErrorCode.INVALID_BOOKMARK_POPUP);
        }
    }
}
