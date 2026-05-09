package com.ncp.team3.popup.port;

import com.ncp.team3.popup.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
