package com.bullionx.portfolioservice.service;

import com.bullionx.portfolioservice.market.FinnhubClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketService {

    private final FinnhubClient finnhub;

    // simple per-symbol cache
    private static final Duration TTL = Duration.ofSeconds(2);
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public MarketService(FinnhubClient finnhub) {
        this.finnhub = finnhub;
    }

    public FinnhubClient.Quote quote(String symbol) {
        final String key = symbol.toUpperCase();
        CacheEntry hit = cache.get(key);
        long now = System.currentTimeMillis();

        if (hit != null && (now - hit.fetchedAtMs) <= TTL.toMillis()) {
            return hit.quote;
        }

        FinnhubClient.Quote fresh = finnhub.getQuote(key);
        cache.put(key, new CacheEntry(fresh, now));
        return fresh;
    }

    private record CacheEntry(FinnhubClient.Quote quote, long fetchedAtMs) {}
}
