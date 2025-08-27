package com.bullionx.portfolioservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class TradeResultDto {
    private String symbol;
    private BigDecimal filledQty;
    private BigDecimal priceUsd;
    private Instant filledAt;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getFilledQty() { return filledQty; }
    public void setFilledQty(BigDecimal filledQty) { this.filledQty = filledQty; }
    public BigDecimal getPriceUsd() { return priceUsd; }
    public void setPriceUsd(BigDecimal priceUsd) { this.priceUsd = priceUsd; }
    public Instant getFilledAt() { return filledAt; }
    public void setFilledAt(Instant filledAt) { this.filledAt = filledAt; }
}
