package com.ncp.team3.bookmark.port;

import com.ncp.team3.bookmark.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    @Query("""
            select count(b) > 0
            from Bookmark b
            where b.member.id = :memberId
              and b.popup.id = :popupId
            """)
    boolean existsByMemberIdAndPopupId(@Param("memberId") Long memberId, @Param("popupId") Long popupId);

    @Query("""
            select distinct b
            from Bookmark b
            join fetch b.popup p
            left join fetch p.popupCategories pc
            left join fetch pc.category
            where b.member.id = :memberId
            order by b.createdAt desc
            """)
    List<Bookmark> findAllByMemberIdWithPopup(@Param("memberId") Long memberId);
}
