package com.bullionx.portfolioservice.controller;

import com.bullionx.portfolioservice.dto.BuyRequest;
import com.bullionx.portfolioservice.dto.SellRequest;
import com.bullionx.portfolioservice.dto.TradeResultDto;
import com.bullionx.portfolioservice.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trades")
public class TradeController {

    private final TradeService trades;

    public TradeController(TradeService trades) {
        this.trades = trades;
    }

    @PostMapping("/buy")
    public ResponseEntity<TradeResultDto> buy(@RequestBody BuyRequest req) {
        return ResponseEntity.ok(trades.buy(req));
    }

    @PostMapping("/sell")
    public ResponseEntity<TradeResultDto> sell(@RequestBody SellRequest req) {
        return ResponseEntity.ok(trades.sell(req));
    }
}
