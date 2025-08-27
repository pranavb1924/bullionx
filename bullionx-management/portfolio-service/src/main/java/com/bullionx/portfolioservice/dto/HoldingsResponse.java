package com.bullionx.portfolioservice.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class HoldingsResponse {
    private List<HoldingView> holdings;
    private PortfolioTotalsDto totals;
    private UUID portfolioId;
    private BigDecimal cashBalance;

    public List<HoldingView> getHoldings() { return holdings; }
    public void setHoldings(List<HoldingView> holdings) { this.holdings = holdings; }
    public PortfolioTotalsDto getTotals() { return totals; }
    public void setTotals(PortfolioTotalsDto totals) { this.totals = totals; }
    public UUID getPortfolioId() { return portfolioId; }
    public void setPortfolioId(UUID portfolioId) { this.portfolioId = portfolioId; }
    public BigDecimal getCashBalance() { return cashBalance; }
    public void setCashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; }
}