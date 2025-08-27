package com.bullionx.portfolioservice.controller;

import com.bullionx.portfolioservice.dto.HoldingsResponse;
import com.bullionx.portfolioservice.service.PortfolioQueryService;
import com.bullionx.portfolioservice.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioQueryService query;
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioQueryService query, PortfolioService portfolioService) {
        this.query = query;
        this.portfolioService = portfolioService;
    }

    @GetMapping("/{portfolioId}/holdings")
    public ResponseEntity<HoldingsResponse> getHoldings(@PathVariable UUID portfolioId) {
        return ResponseEntity.ok(query.getHoldings(portfolioId));
    }

    @GetMapping("/user/{userId}/holdings")
    public ResponseEntity<HoldingsResponse> getHoldingsByUser(@PathVariable UUID userId) {
        UUID portfolioId = portfolioService.getOrCreatePortfolio(userId);
        return ResponseEntity.ok(query.getHoldings(portfolioId));
    }
}
