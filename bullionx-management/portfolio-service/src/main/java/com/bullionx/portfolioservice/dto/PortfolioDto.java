package com.bullionx.portfolioservice.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PortfolioDto {
    private UUID portfolioId;
    private UUID userId;
    private String name;
    private BigDecimal balance;
    private OffsetDateTime createdAt;
}