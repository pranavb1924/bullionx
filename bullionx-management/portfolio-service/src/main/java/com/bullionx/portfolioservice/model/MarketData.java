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
@Table(name = "market_data", indexes = {
        @Index(name = "idx_market_data_symbol", columnList = "symbol", unique = true),
        @Index(name = "idx_market_data_updated", columnList = "last_updated")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketData {

    @Id @UuidGenerator
    @Column(name = "market_data_id", nullable = false, updatable = false)
    private UUID marketDataId;

    @NotNull
    @Column(name = "symbol", nullable = false, length = 12, unique = true)
    private String symbol;

    @Column(name = "current_price", precision = 38, scale = 6)
    private BigDecimal currentPrice;

    @Column(name = "percent_change", precision = 10, scale = 4)
    private BigDecimal percentChange;

    @Column(name = "previous_close", precision = 38, scale = 6)
    private BigDecimal previousClose;

    @Column(name = "high", precision = 38, scale = 6)
    private BigDecimal high;  // Daily high

    @Column(name = "low", precision = 38, scale = 6)
    private BigDecimal low;   // Daily low

    @Column(name = "open", precision = 38, scale = 6)
    private BigDecimal open;  // Daily open

    @Column(name = "volume")
    private Long volume;      // Daily volume

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private OffsetDateTime lastUpdated;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}