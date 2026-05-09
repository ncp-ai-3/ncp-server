package com.ncp.team3.popup.port;

import com.ncp.team3.popup.domain.PopupCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PopupCategoryRepository extends JpaRepository<PopupCategory, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PopupCategory pc where pc.popup.id = :popupId")
    void deleteByPopupId(@Param("popupId") Long popupId);
}
