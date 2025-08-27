package com.bullionx.portfolioservice.dto;

import java.math.BigDecimal;

public class PortfolioTotalsDto {
    private BigDecimal totalValue;
    private BigDecimal totalNetPLAmount;
    private BigDecimal totalNetPLPercent;

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public BigDecimal getTotalNetPLAmount() { return totalNetPLAmount; }
    public void setTotalNetPLAmount(BigDecimal totalNetPLAmount) { this.totalNetPLAmount = totalNetPLAmount; }
    public BigDecimal getTotalNetPLPercent() { return totalNetPLPercent; }
    public void setTotalNetPLPercent(BigDecimal totalNetPLPercent)
    { this.totalNetPLPercent = totalNetPLPercent;
    }
}
