package com.bullionx.portfolioservice.service;

import com.bullionx.portfolioservice.dto.InvestmentView;
import com.bullionx.portfolioservice.market.FinnhubClient;
import com.bullionx.portfolioservice.model.TradeLot;
import com.bullionx.portfolioservice.repository.TradeLotRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Produces the UI-friendly "Investments" rows by aggregating open lots per symbol
 * and combining with live market quotes (USD).
 */
@Service
public class PortfolioQueryService {

    private final TradeLotRepository trades;
    private final MarketService market;

    public PortfolioQueryService(TradeLotRepository trades, MarketService market) {
        this.trades = trades;
        this.market = market;
    }

    public List<InvestmentView> investments(UUID portfolioId) {
        MathContext mc = new MathContext(12, RoundingMode.HALF_UP);

        // Sum open quantities per symbol
        Map<String, BigDecimal> qtyBySymbol = trades.findByPortfolioIdAndSoldAtIsNull(portfolioId)
                .stream()
                .collect(Collectors.groupingBy(
                        TradeLot::getSymbol,
                        Collectors.reducing(BigDecimal.ZERO, TradeLot::getQuantity, BigDecimal::add)
                ));

        // Build rows from quotes
        return qtyBySymbol.entrySet().stream().map(entry -> {
                    String symbol = entry.getKey();
                    BigDecimal qty = entry.getValue();

                    FinnhubClient.Quote q = market.quote(symbol);
                    BigDecimal price = q.price();                    // USD
                    BigDecimal priorClose = q.priorClose();

                    BigDecimal value = price.multiply(qty, mc).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal diff = price.subtract(priorClose, mc);
                    BigDecimal dayGainAmount = diff.multiply(qty, mc).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal dayGainPercent = (priorClose.signum() == 0)
                            ? BigDecimal.ZERO
                            : diff.divide(priorClose, mc).multiply(new BigDecimal("100"), mc)
                            .setScale(2, RoundingMode.HALF_UP);

                    return InvestmentView.builder()
                            .symbol(q.symbol())
                            .name(q.name())
                            .price(price.setScale(2, RoundingMode.HALF_UP))
                            .quantity(qty.setScale(0, RoundingMode.HALF_UP))
                            .dayGainAmount(dayGainAmount)
                            .dayGainPercent(dayGainPercent)
                            .value(value)
                            .currency("USD")
                            .build();
                }).sorted(Comparator.comparing(InvestmentView::getSymbol))
                .toList();
    }
}
