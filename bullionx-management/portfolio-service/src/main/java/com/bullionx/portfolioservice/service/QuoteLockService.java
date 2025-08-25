package com.bullionx.portfolioservice.service;

import com.bullionx.portfolioservice.market.FinnhubClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuoteLockService {

    public record Lock(UUID id, String symbol, BigDecimal priceUsd, Instant expiresAt) {}

    private static final Duration TTL = Duration.ofSeconds(10);

    private final MarketService market;
    private final Map<UUID, Lock> locks = new ConcurrentHashMap<>();

    public QuoteLockService(MarketService market) {
        this.market = market;
    }

    /** Create a new lock at the current market USD price. */
    public Lock create(String symbol) {
        FinnhubClient.Quote q = market.quote(symbol);
        Lock lock = new Lock(UUID.randomUUID(), q.symbol(), q.price(), Instant.now().plus(TTL));
        locks.put(lock.id(), lock);
        return lock;
    }

    public Lock consume(UUID id, String symbol) {
        Lock lock = locks.remove(id);
        if (lock == null) throw new IllegalStateException("Lock not found or already used");
        if (!lock.symbol().equalsIgnoreCase(symbol)) throw new IllegalStateException("Symbol mismatch");
        if (Instant.now().isAfter(lock.expiresAt())) throw new IllegalStateException("Lock expired");
        return lock;
    }
}
