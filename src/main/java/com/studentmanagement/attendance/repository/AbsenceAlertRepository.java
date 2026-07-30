package com.studentmanagement.attendance.repository;

import com.studentmanagement.attendance.model.AbsenceAlert;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbsenceAlertRepository extends JpaRepository<AbsenceAlert, UUID> {

    List<AbsenceAlert> findByResolvedFalse();

}
