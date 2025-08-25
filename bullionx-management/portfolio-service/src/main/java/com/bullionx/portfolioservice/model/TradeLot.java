package com.bullionx.portfolioservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "trades", indexes = {
        @Index(name = "idx_trades_portfolio", columnList = "portfolio_id"),
        @Index(name = "idx_trades_symbol", columnList = "symbol"),
        @Index(name = "idx_trades_purchased_at", columnList = "purchased_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TradeLot {

    @Id @UuidGenerator
    @Column(name = "trade_id", nullable = false, updatable = false)
    private UUID tradeId;

    @NotNull
    @Column(name = "portfolio_id", nullable = false)
    private UUID portfolioId;

    @NotNull
    @Column(name = "symbol", nullable = false, length = 12)
    private String symbol;

    @NotNull @Positive
    @Column(name = "quantity", nullable = false, precision = 38, scale = 8)
    private BigDecimal quantity;

    // USD-only pricing
    @NotNull
    @Column(name = "purchase_price_usd", nullable = false, precision = 38, scale = 6)
    private BigDecimal purchasePriceUsd;

    @NotNull
    @Column(name = "purchased_at", nullable = false)
    private OffsetDateTime purchasedAt;

    @Column(name = "sell_price_usd", precision = 38, scale = 6)
    private BigDecimal sellPriceUsd; // null while open

    @Column(name = "sold_at")
    private OffsetDateTime soldAt;   // null while open

    @Transient
    public boolean isOpen() { return soldAt == null; }
}
