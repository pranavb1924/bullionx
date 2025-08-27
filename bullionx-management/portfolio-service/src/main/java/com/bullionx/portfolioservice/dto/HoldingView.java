package com.bullionx.portfolioservice.dto;

import java.math.BigDecimal;

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

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getAverageCost() { return averageCost; }
    public void setAverageCost(BigDecimal averageCost) { this.averageCost = averageCost; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public BigDecimal getDayGainAmount() { return dayGainAmount; }
    public void setDayGainAmount(BigDecimal dayGainAmount) { this.dayGainAmount = dayGainAmount; }
    public BigDecimal getDayGainPercent() { return dayGainPercent; }
    public void setDayGainPercent(BigDecimal dayGainPercent) { this.dayGainPercent = dayGainPercent; }
    public BigDecimal getNetPLAmount() { return netPLAmount; }
    public void setNetPLAmount(BigDecimal netPLAmount) { this.netPLAmount = netPLAmount; }
    public BigDecimal getNetPLPercent() { return netPLPercent; }
    public void setNetPLPercent(BigDecimal netPLPercent) { this.netPLPercent = netPLPercent; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
