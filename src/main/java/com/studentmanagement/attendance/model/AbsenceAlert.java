package com.studentmanagement.attendance.model;

import com.studentmanagement.student.model.Student;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "absence_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AbsenceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal threshold = BigDecimal.valueOf(75.00);

    @Column(name = "current_percentage", precision = 5, scale = 2)
    private BigDecimal currentPercentage;

    @Column(name = "alerted_at", nullable = false)
    private Instant alertedAt;

    @Column(nullable = false)
    private Boolean resolved = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
