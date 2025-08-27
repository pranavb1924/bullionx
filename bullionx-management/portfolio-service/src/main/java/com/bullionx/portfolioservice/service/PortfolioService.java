package com.bullionx.portfolioservice.service;

import com.bullionx.portfolioservice.model.Portfolio;
import com.bullionx.portfolioservice.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Transactional
    public UUID getOrCreatePortfolio(UUID userId) {
        return portfolioRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)
                .map(Portfolio::getPortfolioId)
                .orElseGet(() -> {
                    Portfolio portfolio = Portfolio.builder()
                            .userId(userId)
                            .name("My Portfolio")
                            .balance(new BigDecimal("10000.00"))
                            .build();
                    Portfolio saved = portfolioRepository.save(portfolio);
                    System.out.println("Created new portfolio for user: " + userId);
                    return saved.getPortfolioId();
                });
    }
}