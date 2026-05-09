package com.ncp.team3.bookmark.port;

import com.ncp.team3.bookmark.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    @Query("""
            select count(b) > 0
            from Bookmark b
            where b.member.id = :memberId
              and b.popup.id = :popupId
            """)
    boolean existsByMemberIdAndPopupId(@Param("memberId") Long memberId, @Param("popupId") Long popupId);
}
