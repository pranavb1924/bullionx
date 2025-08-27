package com.bullionx.portfolioservice.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreatePortfolioRequest {
    private UUID userId;
}