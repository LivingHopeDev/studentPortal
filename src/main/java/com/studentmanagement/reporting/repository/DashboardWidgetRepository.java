package com.studentmanagement.reporting.repository;

import com.studentmanagement.reporting.model.DashboardWidget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DashboardWidgetRepository extends JpaRepository<DashboardWidget, UUID> {
}
