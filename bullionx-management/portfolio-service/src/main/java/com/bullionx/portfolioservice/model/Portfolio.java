package com.bullionx.portfolioservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "portfolios", indexes = @Index(name = "idx_portfolio_user", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portfolio {

    @Id @UuidGenerator
    @Column(name = "portfolio_id", nullable = false, updatable = false)
    private UUID portfolioId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "name", nullable = false, length = 120)
    private String name;  // FIXED: This should be String, not BigDecimal

    @NotNull
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = new BigDecimal("10000.00");  // ADD THIS FIELD with default

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}