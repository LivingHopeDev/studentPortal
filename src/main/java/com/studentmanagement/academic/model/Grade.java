package com.studentmanagement.academic.model;

import com.studentmanagement.auth.model.User;
import com.studentmanagement.common.enums.GradeStatus;
import com.studentmanagement.student.model.Student;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "grades", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id", "semester_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "letter_grade", length = 5)
    private String letterGrade;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GradeStatus status = GradeStatus.DRAFT;

    @Column(name = "published_at")
    private Instant publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_by")
    private User publishedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
