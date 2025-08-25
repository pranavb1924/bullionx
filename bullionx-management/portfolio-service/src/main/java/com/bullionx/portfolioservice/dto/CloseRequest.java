package com.bullionx.portfolioservice.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CloseRequest(
        @NotNull BigDecimal sellPriceUsd,
        OffsetDateTime soldAt 
) {}
