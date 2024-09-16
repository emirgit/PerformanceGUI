package com.performance.gui.repository;

import com.performance.gui.model.SystemData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemDataRepository extends JpaRepository<SystemData, Long> {
}
