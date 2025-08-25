package com.bullionx.portfolioservice.controller;

import com.bullionx.portfolioservice.dto.InvestmentView;
import com.bullionx.portfolioservice.service.PortfolioQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioQueryService svc;

    public PortfolioController(PortfolioQueryService svc) {
        this.svc = svc;
    }

    // One row per symbol with price, qty, day gain, value (USD)
    @GetMapping("/{portfolioId}/investments")
    public ResponseEntity<List<InvestmentView>> investments(@PathVariable UUID portfolioId) {
        return ResponseEntity.ok(svc.investments(portfolioId));
    }
}
