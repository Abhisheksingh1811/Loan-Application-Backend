package com.abhisheksingh.secureloangateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String existingCorrelationId =
                exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);

        String correlationId = isBlank(existingCorrelationId)
                ? UUID.randomUUID().toString()
                : existingCorrelationId;

        ServerHttpRequest requestWithCorrelationId = exchange.getRequest()
                .mutate()
                .headers(headers -> headers.set(CORRELATION_ID_HEADER, correlationId))
                .build();

        ServerWebExchange exchangeWithCorrelationId = exchange
                .mutate()
                .request(requestWithCorrelationId)
                .build();

        exchangeWithCorrelationId.getResponse().beforeCommit(() -> {
            exchangeWithCorrelationId.getResponse()
                    .getHeaders()
                    .set(CORRELATION_ID_HEADER, correlationId);
            return Mono.empty();
        });

        log.info(
                "Gateway request | correlationId={} | method={} | path={}",
                correlationId,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath()
        );

        return chain.filter(exchangeWithCorrelationId);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
