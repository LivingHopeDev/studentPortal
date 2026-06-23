package com.studentmanagement.fees.model;

import com.studentmanagement.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 50)
    private String gateway;

    @Column(name = "gateway_reference")
    private String gatewayReference;

    @Column(nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "paid_at")
    private Instant paidAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
