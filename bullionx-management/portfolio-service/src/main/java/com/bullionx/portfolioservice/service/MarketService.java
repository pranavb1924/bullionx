package com.bullionx.portfolioservice.service;

import com.bullionx.portfolioservice.dto.StockDetailsDto;
import com.bullionx.portfolioservice.market.FinnhubClient;
import com.bullionx.portfolioservice.mapper.StockMapper;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MarketService {

    private final FinnhubClient finnhub;

    public MarketService(FinnhubClient finnhub) {
        this.finnhub = finnhub;
    }

    public StockDetailsDto getStockDetails(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("symbol is required");
        String sym = symbol.trim().toUpperCase(Locale.ROOT);
        var q = finnhub.quote(sym);
        return StockMapper.toStockDetails(sym, q);
    }
}
