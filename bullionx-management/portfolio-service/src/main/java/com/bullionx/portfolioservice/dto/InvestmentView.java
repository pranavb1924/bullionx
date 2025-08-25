package com.bullionx.portfolioservice.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvestmentView {
    private String symbol;
    private String name;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal dayGainAmount;
    private BigDecimal dayGainPercent;
    private BigDecimal value;
    private String currency;
}
