package com.bullionx.portfolioservice.controller;

import com.bullionx.portfolioservice.dto.StockDetailsDto;
import com.bullionx.portfolioservice.service.MarketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/market")
public class MarketController {

    private static final Logger log = LoggerFactory.getLogger(MarketController.class);
    private final MarketService market;

    public MarketController(MarketService market) {
        this.market = market;
    }

    @GetMapping("/stock")
    public ResponseEntity<StockDetailsDto> getStock(@RequestParam String symbol) {
        StockDetailsDto dto = market.getStockDetails(symbol);
        log.info("Stock details for '{}': {}", symbol, dto);
        return ResponseEntity.ok(dto);
    }
}
