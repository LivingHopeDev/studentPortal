package com.studentmanagement.communication.model;

import com.studentmanagement.common.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String recipient;

    @Column(length = 500)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EmailStatus status = EmailStatus.PENDING;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
