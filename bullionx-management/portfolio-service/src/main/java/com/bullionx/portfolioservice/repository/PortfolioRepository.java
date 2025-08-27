package com.bullionx.portfolioservice.repository;

import com.bullionx.portfolioservice.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    Optional<Portfolio> findFirstByUserIdOrderByCreatedAtAsc(UUID userId);
}
