package com.bullionx.portfolioservice.mapper;

import com.bullionx.portfolioservice.dto.StockDetailsDto;
import com.bullionx.portfolioservice.market.FinnhubClient;

public final class StockMapper {
    private StockMapper() {}

    public static StockDetailsDto toStockDetails(String symbol, FinnhubClient.Quote q) {
        StockDetailsDto dto = new StockDetailsDto();
        dto.setSymbol(symbol);
        if (q != null) {
            dto.setCurrentPrice(nz(q.c()));
            dto.setPreviousClose(nz(q.pc()));
            dto.setChange(nz(q.d()));
            dto.setPercentChange(nz(q.dp()));
            dto.setHigh(nz(q.h()));
            dto.setLow(nz(q.l()));
            dto.setOpen(nz(q.o()));
            dto.setTimestamp(q.t());
        }
        return dto;
    }

    private static Double nz(Double v) {
        return v == null ? 0.0 : v;
    }
}
