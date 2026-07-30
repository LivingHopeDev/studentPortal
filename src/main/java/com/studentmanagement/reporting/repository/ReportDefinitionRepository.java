package com.studentmanagement.reporting.repository;

import com.studentmanagement.reporting.model.ReportDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportDefinitionRepository extends JpaRepository<ReportDefinition, UUID> {
}
