package com.bullionx.portfolioservice.market;

public interface FinnhubClient {

    record Quote(
            Double c,
            Double d,
            Double dp,
            Double h,
            Double l,
            Double o,
            Double pc,
            Long t
    ) {}

    Quote quote(String symbol);
}
