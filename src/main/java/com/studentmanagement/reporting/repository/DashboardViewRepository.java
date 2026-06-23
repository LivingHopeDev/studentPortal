package com.studentmanagement.reporting.repository;

import com.studentmanagement.reporting.model.DashboardView;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardViewRepository extends JpaRepository<DashboardView, UUID> {

}
