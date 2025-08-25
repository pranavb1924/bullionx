package com.bullionx.portfolioservice.controller;

import com.bullionx.portfolioservice.market.FinnhubClient;
import com.bullionx.portfolioservice.service.MarketService;
import com.bullionx.portfolioservice.service.QuoteLockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/market")
public class MarketController {

    private final MarketService market;
    private final QuoteLockService locks;

    public MarketController(MarketService market, QuoteLockService locks) {
        this.market = market;
        this.locks = locks;
    }

    // Poll from frontend: /market/quotes?symbols=AAPL,MSFT,TSLA
    @GetMapping("/quotes")
    public ResponseEntity<List<FinnhubClient.Quote>> quotes(@RequestParam String symbols) {
        var list = Arrays.stream(symbols.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(market::quote)
                .toList();
        return ResponseEntity.ok(list);
    }

    // Lock the current USD price for a quick buy flow (expires after ~10s)
    // POST /market/locks?symbol=AAPL
    @PostMapping("/locks")
    public ResponseEntity<QuoteLockService.Lock> createLock(@RequestParam String symbol) {
        return ResponseEntity.ok(locks.create(symbol));
    }
}
