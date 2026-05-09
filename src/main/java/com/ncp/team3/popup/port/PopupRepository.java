package com.ncp.team3.popup.port;

import com.ncp.team3.popup.domain.Popup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PopupRepository extends JpaRepository<Popup, Long> {
    @EntityGraph(attributePaths = {"popupCategories", "popupCategories.category"})
    @Query("select p from Popup p where p.id = :id")
    Optional<Popup> findDetailById(@Param("id") Long id);
}
