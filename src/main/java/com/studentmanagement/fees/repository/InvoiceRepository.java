package com.studentmanagement.fees.repository;

import com.studentmanagement.common.enums.InvoiceStatus;
import com.studentmanagement.fees.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    List<Invoice> findByStudentId(UUID studentId);

    List<Invoice> findByStudentIdOrderByCreatedAtDesc(UUID studentId);

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByStatusOrderByCreatedAtDesc(InvoiceStatus status);

    List<Invoice> findAllByOrderByCreatedAtDesc();
}
