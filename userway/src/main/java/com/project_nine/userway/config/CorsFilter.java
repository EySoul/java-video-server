package com.project_nine.userway.config;

import java.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CorsFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response
            .getHeaders()
            .addAll(
                HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                Arrays.asList(
                    HttpHeaders.CONTENT_RANGE,
                    HttpHeaders.ACCEPT_RANGES,
                    HttpHeaders.CONTENT_TYPE
                )
            );
        return chain.filter(exchange);
    }
}
