package com.ncp.team3.popup.port;

import com.ncp.team3.popup.domain.PopupCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopupCategoryRepository extends JpaRepository<PopupCategory, Long> {
}
