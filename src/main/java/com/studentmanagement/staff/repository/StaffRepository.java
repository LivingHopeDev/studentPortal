package com.studentmanagement.staff.repository;

import com.studentmanagement.staff.model.Staff;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

}
