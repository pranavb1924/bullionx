package com.bullionx.portfolioservice.dto;

public class StockDetailsDto {
    private String symbol;
    private Double currentPrice;
    private Double previousClose;
    private Double change;
    private Double percentChange;
    private Double high;
    private Double low;
    private Double open;
    private Long timestamp;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public Double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(Double currentPrice) { this.currentPrice = currentPrice; }
    public Double getPreviousClose() { return previousClose; }
    public void setPreviousClose(Double previousClose) { this.previousClose = previousClose; }
    public Double getChange() { return change; }
    public void setChange(Double change) { this.change = change; }
    public Double getPercentChange() { return percentChange; }
    public void setPercentChange(Double percentChange) { this.percentChange = percentChange; }
    public Double getHigh() { return high; }
    public void setHigh(Double high) { this.high = high; }
    public Double getLow() { return low; }
    public void setLow(Double low) { this.low = low; }
    public Double getOpen() { return open; }
    public void setOpen(Double open) { this.open = open; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
