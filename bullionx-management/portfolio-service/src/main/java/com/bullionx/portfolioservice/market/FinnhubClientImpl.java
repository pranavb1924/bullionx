package com.bullionx.portfolioservice.market;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class FinnhubClientImpl implements FinnhubClient {

    private final WebClient http;
    private final String apiKey;

    public FinnhubClientImpl(
            @Value("${finnhub.base-url:https://finnhub.io/api/v1}") String baseUrl,
            @Value("${finnhub.api.key}") String apiKey
    ) {
        this.http = WebClient.builder().baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    @Override
    public Quote quote(String symbol) {
        try {
            return http.get()
                    .uri(uri -> uri.path("/quote").queryParam("symbol", symbol).queryParam("token", apiKey).build())
                    .retrieve()
                    .bodyToMono(Quote.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new RuntimeException("Finnhub rate limit exceeded", e);
            }
            throw new RuntimeException("Finnhub /quote failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Finnhub /quote failed", e);
        }
    }
}
