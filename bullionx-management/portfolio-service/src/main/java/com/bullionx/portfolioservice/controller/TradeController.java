package com.bullionx.portfolioservice.controller;

import com.bullionx.portfolioservice.dto.BuyRequest;
import com.bullionx.portfolioservice.dto.CloseRequest;
import com.bullionx.portfolioservice.model.TradeLot;
import com.bullionx.portfolioservice.repository.TradeLotRepository;
import com.bullionx.portfolioservice.service.QuoteLockService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/portfolios/{portfolioId}/trades")
public class TradeController {

    private final TradeLotRepository repo;
    private final QuoteLockService locks;

    public TradeController(TradeLotRepository repo, QuoteLockService locks) {
        this.repo = repo;
        this.locks = locks;
    }

    // BUY a new lot at the locked USD price
    @PostMapping
    public ResponseEntity<TradeLot> buy(@PathVariable UUID portfolioId, @Valid @RequestBody BuyRequest req) {
        var lock = locks.consume(req.lockId(), req.symbol()); // throws if expired/missing
        TradeLot lot = TradeLot.builder()
                .portfolioId(portfolioId)
                .symbol(req.symbol())
                .quantity(req.quantity())
                .purchasePriceUsd(lock.priceUsd())
                .purchasedAt(OffsetDateTime.now())
                .build();
        return ResponseEntity.ok(repo.save(lot));
    }

    // SELL (close the entire lot)
    @PostMapping("/{tradeId}/close")
    public ResponseEntity<TradeLot> close(@PathVariable UUID portfolioId,
                                          @PathVariable UUID tradeId,
                                          @Valid @RequestBody CloseRequest req) {
        var lot = repo.findById(tradeId).orElseThrow();
        if (!lot.getPortfolioId().equals(portfolioId)) throw new IllegalArgumentException("Wrong portfolio");
        if (lot.getSoldAt() != null) throw new IllegalStateException("Lot already closed");

        lot.setSellPriceUsd(req.sellPriceUsd());
        lot.setSoldAt(req.soldAt() != null ? req.soldAt() : OffsetDateTime.now());
        return ResponseEntity.ok(repo.save(lot));
    }
}
