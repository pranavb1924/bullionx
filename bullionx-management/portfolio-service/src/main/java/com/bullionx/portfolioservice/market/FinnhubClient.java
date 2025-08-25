package com.bullionx.portfolioservice.market;

import java.math.BigDecimal;

public interface FinnhubClient {


    record Quote(String symbol, String name, BigDecimal price, BigDecimal priorClose, long epochSec) {}
    Quote getQuote(String symbol);
}
