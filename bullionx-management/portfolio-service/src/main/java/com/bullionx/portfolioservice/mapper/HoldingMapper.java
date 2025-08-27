package com.bullionx.portfolioservice.mapper;

import com.bullionx.portfolioservice.dto.HoldingView;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class HoldingMapper {
    private HoldingMapper() {}

    public static HoldingView toHolding(
            String symbol,
            BigDecimal quantity,
            BigDecimal averageCost,
            BigDecimal currentPrice,
            BigDecimal value,
            BigDecimal dayGainAmount,
            BigDecimal dayGainPercent,
            BigDecimal netPLAmount,
            BigDecimal netPLPercent
    ) {
        HoldingView v = new HoldingView();
        v.setSymbol(symbol);
        v.setQuantity(scale0(quantity));
        v.setAverageCost(scale2(averageCost));
        v.setCurrentPrice(scale2(currentPrice));
        v.setValue(scale2(value));
        v.setDayGainAmount(scale2(dayGainAmount));
        v.setDayGainPercent(scale2(dayGainPercent));
        v.setNetPLAmount(scale2(netPLAmount));
        v.setNetPLPercent(scale2(netPLPercent));
        v.setCurrency("USD");
        return v;
    }

    private static BigDecimal scale2(BigDecimal v) {
        return v == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : v.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal scale0(BigDecimal v) {
        return v == null ? BigDecimal.ZERO.setScale(0, RoundingMode.HALF_UP) : v.setScale(0, RoundingMode.HALF_UP);
    }
}
