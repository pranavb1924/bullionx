package com.bullionx.portfolioservice.service;

import com.bullionx.portfolioservice.dto.BuyRequest;
import com.bullionx.portfolioservice.dto.SellRequest;
import com.bullionx.portfolioservice.dto.TradeResultDto;
import com.bullionx.portfolioservice.market.FinnhubClient;
import com.bullionx.portfolioservice.model.Portfolio;
import com.bullionx.portfolioservice.model.TradeLot;
import com.bullionx.portfolioservice.repository.PortfolioRepository;
import com.bullionx.portfolioservice.repository.TradeLotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class TradeService {

    private final TradeLotRepository trades;
    private final FinnhubClient finnhub;
    private final PortfolioRepository portfolioRepo;

    public TradeService(TradeLotRepository trades, FinnhubClient finnhub, PortfolioRepository portfolioRepo) {
        this.trades = trades;
        this.finnhub = finnhub;
        this.portfolioRepo = portfolioRepo;
    }

    @Transactional
    public TradeResultDto buy(BuyRequest req) {
        // Get portfolio and check it exists
        Portfolio portfolio = portfolioRepo.findById(req.getPortfolioId())
                .orElseThrow(() -> new IllegalStateException("Portfolio not found: " + req.getPortfolioId()));

        validateQty(req.getQuantity());
        String sym = req.getSymbol().trim().toUpperCase(Locale.ROOT);

        // Get current market price
        var q = finnhub.quote(sym);
        if (q == null || q.c() == null || q.c() <= 0) {
            throw new IllegalStateException("Price not available for " + sym);
        }

        BigDecimal execPriceStore = new BigDecimal(q.c().toString()).setScale(6, RoundingMode.HALF_UP);
        BigDecimal execPriceView = execPriceStore.setScale(2, RoundingMode.HALF_UP);

        // Calculate total cost
        BigDecimal totalCost = execPriceStore.multiply(req.getQuantity()).setScale(2, RoundingMode.HALF_UP);

        // Check sufficient funds
        if (portfolio.getBalance().compareTo(totalCost) < 0) {
            throw new IllegalStateException(String.format(
                    "Insufficient funds. Required: $%s, Available: $%s",
                    totalCost.toPlainString(),
                    portfolio.getBalance().toPlainString()
            ));
        }

        OffsetDateTime now = OffsetDateTime.now();

        // Create the trade lot
        TradeLot lot = TradeLot.builder()
                .portfolioId(req.getPortfolioId())
                .symbol(sym)
                .quantity(req.getQuantity())
                .purchasePriceUsd(execPriceStore)
                .purchasedAt(now)
                .sellPriceUsd(null)
                .soldAt(null)
                .build();
        trades.save(lot);

        // Deduct from balance
        portfolio.setBalance(portfolio.getBalance().subtract(totalCost));
        portfolioRepo.save(portfolio);

        // Return result
        TradeResultDto res = new TradeResultDto();
        res.setSymbol(sym);
        res.setFilledQty(req.getQuantity());
        res.setPriceUsd(execPriceView);
        res.setFilledAt(now.toInstant());
        return res;
    }

    @Transactional
    public TradeResultDto sell(SellRequest req) {
        // Get portfolio
        Portfolio portfolio = portfolioRepo.findById(req.getPortfolioId())
                .orElseThrow(() -> new IllegalStateException("Portfolio not found: " + req.getPortfolioId()));

        validateQty(req.getQuantity());
        String sym = req.getSymbol().trim().toUpperCase(Locale.ROOT);

        // Check available quantity to sell
        List<TradeLot> open = trades.findByPortfolioIdAndSymbolAndSoldAtIsNullOrderByPurchasedAtAsc(
                req.getPortfolioId(), sym);

        BigDecimal available = BigDecimal.ZERO;
        for (TradeLot l : open) {
            available = available.add(nz(l.getQuantity()));
        }

        if (available.compareTo(req.getQuantity()) < 0) {
            throw new IllegalStateException(String.format(
                    "Not enough quantity to sell. Requested: %s, Available: %s",
                    req.getQuantity().toPlainString(),
                    available.toPlainString()
            ));
        }

        // Get current market price
        var q = finnhub.quote(sym);
        if (q == null || q.c() == null || q.c() <= 0) {
            throw new IllegalStateException("Price not available for " + sym);
        }

        BigDecimal execPriceStore = new BigDecimal(q.c().toString()).setScale(6, RoundingMode.HALF_UP);
        BigDecimal execPriceView = execPriceStore.setScale(2, RoundingMode.HALF_UP);
        OffsetDateTime now = OffsetDateTime.now();

        BigDecimal remaining = req.getQuantity();

        // Process lots FIFO
        for (TradeLot lot : open) {
            if (remaining.signum() == 0) break;

            BigDecimal lotQty = nz(lot.getQuantity());

            if (lotQty.compareTo(remaining) <= 0) {
                // Sell entire lot
                lot.setSellPriceUsd(execPriceStore);
                lot.setSoldAt(now);
                remaining = remaining.subtract(lotQty);
                trades.save(lot);
            } else {
                // Partial sell - split the lot
                BigDecimal keepOpenQty = lotQty.subtract(remaining);

                // Create new lot for the unsold portion
                TradeLot openRemainder = TradeLot.builder()
                        .portfolioId(lot.getPortfolioId())
                        .symbol(lot.getSymbol())
                        .quantity(keepOpenQty)
                        .purchasePriceUsd(lot.getPurchasePriceUsd())
                        .purchasedAt(lot.getPurchasedAt())
                        .sellPriceUsd(null)
                        .soldAt(null)
                        .build();

                // Update current lot to sold quantity
                lot.setQuantity(remaining);
                lot.setSellPriceUsd(execPriceStore);
                lot.setSoldAt(now);

                trades.save(lot);
                trades.save(openRemainder);
                remaining = BigDecimal.ZERO;
            }
        }

        // Calculate proceeds and add to balance
        BigDecimal proceeds = execPriceView.multiply(req.getQuantity()).setScale(2, RoundingMode.HALF_UP);
        portfolio.setBalance(portfolio.getBalance().add(proceeds));
        portfolioRepo.save(portfolio);

        // Return result
        TradeResultDto res = new TradeResultDto();
        res.setSymbol(sym);
        res.setFilledQty(req.getQuantity());
        res.setPriceUsd(execPriceView);
        res.setFilledAt(now.toInstant());
        return res;
    }

    private static void validateQty(BigDecimal q) {
        if (q == null || q.signum() <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}