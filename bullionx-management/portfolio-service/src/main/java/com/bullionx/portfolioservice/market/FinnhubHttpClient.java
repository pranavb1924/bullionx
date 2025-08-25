package com.bullionx.portfolioservice.market;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Finnhub HTTP client (REST). We only call /quote for now.
 * - Returns USD prices.
 * - Soft-fails in dev (returns zero quote) if provider errors; keeps UI responsive.
 */
@Component
public class FinnhubHttpClient implements FinnhubClient {

    private final WebClient http;
    private final String baseUrl;
    private final String token;

    public FinnhubHttpClient(
            WebClient http,
            @Value("${finnhub.base-url:https://finnhub.io/api/v1}") String baseUrl,
            @Value("${finnhub.token:DEMO_NO_KEY}") String token
    ) {
        this.http = http;
        this.baseUrl = baseUrl;
        this.token = token;
    }

    @Override
    public Quote getQuote(String symbol) {
        final String sym = symbol.toUpperCase();

        try {
            Map<?, ?> body = http.get()
                    .uri(baseUrl + "/quote?symbol={s}&token={t}", sym, token)
                    .retrieve()
                    // ✅ return Mono<Throwable>, not Mono.error(...)
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .map(msg -> new RuntimeException("Finnhub error: " + resp.statusCode() + " " + msg))
                    )
                    .bodyToMono(Map.class)
                    .block();

            if (body == null) return zero(sym);

            BigDecimal c  = toBigDecimal(body.get("c"));   // current
            BigDecimal pc = toBigDecimal(body.get("pc"));  // prior close
            long t        = toLong(body.get("t"));

            return new Quote(sym, sym, c, pc, t);
        } catch (Exception e) {
            // Soft-fail for dev/demo to avoid crashing the UI if token is missing/expired
            return zero(sym);
        }
    }

    private static BigDecimal toBigDecimal(Object v) {
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof Number n) return new BigDecimal(n.toString());
        return new BigDecimal(String.valueOf(v));
    }

    private static long toLong(Object v) {
        if (v == null) return 0L;
        if (v instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(v)); } catch (Exception ignored) { return 0L; }
    }

    private static Quote zero(String sym) {
        return new Quote(sym, sym, BigDecimal.ZERO, BigDecimal.ZERO, 0L);
    }
}
