package com.studentmanagement.staff.repository;

import com.studentmanagement.staff.model.StaffRole;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRoleRepository extends JpaRepository<StaffRole, UUID> {

}
