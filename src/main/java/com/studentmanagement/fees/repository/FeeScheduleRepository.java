package com.studentmanagement.fees.repository;

import com.studentmanagement.fees.model.FeeSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeeScheduleRepository extends JpaRepository<FeeSchedule, UUID> {
}
