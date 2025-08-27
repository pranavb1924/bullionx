package com.bullionx.portfolioservice.repository;

import com.bullionx.portfolioservice.model.TradeLot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TradeLotRepository extends JpaRepository<TradeLot, UUID> {
    List<TradeLot> findByPortfolioIdAndSoldAtIsNull(UUID portfolioId);
    List<TradeLot> findByPortfolioIdAndSymbolAndSoldAtIsNullOrderByPurchasedAtAsc(UUID portfolioId, String symbol);
}
