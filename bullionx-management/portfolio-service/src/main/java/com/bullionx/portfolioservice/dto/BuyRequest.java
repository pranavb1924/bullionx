package com.bullionx.portfolioservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record BuyRequest(
        @NotNull String symbol,
        @NotNull @Positive BigDecimal quantity,
        @NotNull UUID lockId
) {}
