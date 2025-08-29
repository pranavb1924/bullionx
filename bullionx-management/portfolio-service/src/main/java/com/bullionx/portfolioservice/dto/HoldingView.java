package com.bullionx.portfolioservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class HoldingView {
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal averageCost;
    private BigDecimal currentPrice;
    private BigDecimal value;
    private BigDecimal dayGainAmount;
    private BigDecimal dayGainPercent;
    private BigDecimal netPLAmount;
    private BigDecimal netPLPercent;
    private String currency;

}
