package com.bullionx.portfolioservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class BuyRequest {
    private UUID portfolioId;
    private String symbol;
    private BigDecimal quantity;

    public UUID getPortfolioId() { return portfolioId; }
    public void setPortfolioId(UUID portfolioId) { this.portfolioId = portfolioId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
