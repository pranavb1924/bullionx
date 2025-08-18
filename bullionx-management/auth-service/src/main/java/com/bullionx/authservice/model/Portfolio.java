package com.bullionx.authservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.UUID;

@Entity(name = "Portfolio")
public class Portfolio {

    @Id
    @GeneratedValue()
    private UUID portfolioId;


    private UUID userId;
}
