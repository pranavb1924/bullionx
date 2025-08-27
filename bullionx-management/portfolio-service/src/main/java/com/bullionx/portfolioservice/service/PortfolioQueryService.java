package com.bullionx.portfolioservice.service;

import com.bullionx.portfolioservice.dto.HoldingView;
import com.bullionx.portfolioservice.dto.HoldingsResponse;
import com.bullionx.portfolioservice.dto.PortfolioTotalsDto;
import com.bullionx.portfolioservice.dto.StockDetailsDto;
import com.bullionx.portfolioservice.mapper.HoldingMapper;
import com.bullionx.portfolioservice.model.Portfolio;
import com.bullionx.portfolioservice.model.TradeLot;
import com.bullionx.portfolioservice.repository.PortfolioRepository;
import com.bullionx.portfolioservice.repository.TradeLotRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

@Service
public class PortfolioQueryService {

    private final TradeLotRepository trades;
    private final MarketService market;
    private final PortfolioRepository portfolios;

    public PortfolioQueryService(TradeLotRepository trades, MarketService market, PortfolioRepository portfolios) {
        this.trades = trades;
        this.market = market;
        this.portfolios = portfolios;
    }

    public HoldingsResponse getHoldingsForUser(UUID userId) {
        UUID portfolioId = ensureDefaultPortfolio(userId);
        return getHoldings(portfolioId);
    }

    public HoldingsResponse getHoldings(UUID portfolioId) {
        MathContext mc = new MathContext(12, RoundingMode.HALF_UP);

        // Get the portfolio for cash balance
        Portfolio portfolio = portfolios.findById(portfolioId)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found: " + portfolioId));

        Map<String, BigDecimal> qtyBySymbol = new HashMap<>();
        Map<String, BigDecimal> costSumBySymbol = new HashMap<>();

        List<TradeLot> openLots = trades.findByPortfolioIdAndSoldAtIsNull(portfolioId);
        for (TradeLot lot : openLots) {
            String symbol = lot.getSymbol();
            if (symbol == null || symbol.isBlank()) continue;
            BigDecimal qty = nz(lot.getQuantity());
            BigDecimal price = nz(lot.getPurchasePriceUsd());

            BigDecimal prevQty = qtyBySymbol.get(symbol);
            if (prevQty == null) prevQty = BigDecimal.ZERO;
            qtyBySymbol.put(symbol, prevQty.add(qty, mc));

            BigDecimal prevCost = costSumBySymbol.get(symbol);
            if (prevCost == null) prevCost = BigDecimal.ZERO;
            costSumBySymbol.put(symbol, prevCost.add(price.multiply(qty, mc), mc));
        }

        List<HoldingView> rows = new ArrayList<>();
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalNetPL = BigDecimal.ZERO;
        BigDecimal totalCostBasis = BigDecimal.ZERO;

        for (Map.Entry<String, BigDecimal> e : qtyBySymbol.entrySet()) {
            String symbol = e.getKey();
            BigDecimal qty = e.getValue();
            if (qty.signum() == 0) continue;

            BigDecimal costSum = costSumBySymbol.get(symbol);
            BigDecimal avgCost = qty.signum() == 0 ? BigDecimal.ZERO : costSum.divide(qty, mc);

            StockDetailsDto s = market.getStockDetails(symbol);
            BigDecimal current = bd(s.getCurrentPrice(), mc);
            BigDecimal prevClose = bd(s.getPreviousClose(), mc);

            BigDecimal value = current.multiply(qty, mc);
            BigDecimal dayDiff = current.subtract(prevClose, mc);
            BigDecimal dayAmt = dayDiff.multiply(qty, mc);

            BigDecimal netAmt = current.subtract(avgCost, mc).multiply(qty, mc);
            BigDecimal netPct = avgCost.signum() == 0
                    ? BigDecimal.ZERO
                    : current.subtract(avgCost, mc).divide(avgCost, mc).multiply(new BigDecimal("100"), mc);

            BigDecimal dayPct;
            if (s.getPercentChange() != null && Math.abs(s.getPercentChange()) > 0.000001) {
                dayPct = new BigDecimal(s.getPercentChange().toString(), mc);
            } else if (prevClose.signum() != 0) {
                dayPct = dayDiff.divide(prevClose, mc).multiply(new BigDecimal("100"), mc);
            } else {
                dayPct = BigDecimal.ZERO;
            }

            HoldingView row = HoldingMapper.toHolding(
                    symbol.toUpperCase(Locale.ROOT),
                    qty,
                    avgCost,
                    current,
                    value,
                    dayAmt,
                    dayPct,
                    netAmt,
                    netPct
            );
            rows.add(row);

            totalValue = totalValue.add(row.getValue(), mc);
            totalNetPL = totalNetPL.add(row.getNetPLAmount(), mc);
            totalCostBasis = totalCostBasis.add(avgCost.multiply(qty, mc), mc);
        }

        rows.sort(Comparator.comparing(HoldingView::getSymbol));

        PortfolioTotalsDto totals = new PortfolioTotalsDto();
        totals.setTotalValue(scale2(totalValue));
        totals.setTotalNetPLAmount(scale2(totalNetPL));
        BigDecimal totalNetPLPercent = totalCostBasis.signum() == 0
                ? BigDecimal.ZERO
                : totalNetPL.divide(totalCostBasis, mc).multiply(new BigDecimal("100"), mc);
        totals.setTotalNetPLPercent(scale2(totalNetPLPercent));

        HoldingsResponse response = new HoldingsResponse();
        response.setHoldings(rows);
        response.setTotals(totals);
        response.setPortfolioId(portfolioId);
        response.setCashBalance(portfolio.getBalance()); // ADD THIS LINE
        return response;
    }

    private UUID ensureDefaultPortfolio(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        var existing = portfolios.findFirstByUserIdOrderByCreatedAtAsc(userId);
        if (existing.isPresent()) return existing.get().getPortfolioId();
        Portfolio p = Portfolio.builder().userId(userId).name("My Portfolio").build();
        p = portfolios.save(p);
        return p.getPortfolioId();
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static BigDecimal bd(Double v, MathContext mc) {
        return v == null ? BigDecimal.ZERO : new BigDecimal(v.toString(), mc);
    }

    private static BigDecimal scale2(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
