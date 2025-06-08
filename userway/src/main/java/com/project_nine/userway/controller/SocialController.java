package com.project_nine.userway.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * SocialController
 */
@RestController
@RequestMapping("/api/social")
public class SocialController {

    private final WebClient webClient;

    public SocialController(WebClient socialWebClient) {
        this.webClient = socialWebClient;
    }

    @RequestMapping("/like/**")
    public Mono<ResponseEntity<String>> toLike(
        @RequestBody(required = false) JsonNode rawJsonBody,
        ServerWebExchange exchange
    ) {
        // 1. Добавляем новые поля в JSON
        ObjectNode modifiedBody = ((ObjectNode) rawJsonBody);
        return exchange
            .getPrincipal()
            .flatMap(princiapal -> {
                // System.out.println(exchange.getRequest().getPath());
                return webClient
                    .method(exchange.getRequest().getMethod())
                    .uri("/api" + exchange.getRequest().getPath().subPath(3))
                    .bodyValue(
                        modifiedBody.put("username", princiapal.getName())
                    )
                    .retrieve()
                    .toEntity(String.class);
            });
    }

    @RequestMapping("/comment/**")
    public Mono<ResponseEntity<String>> toComment(
        @RequestBody(required = false) JsonNode rawJsonBody,
        ServerWebExchange exchange
    ) {
        // 1. Добавляем новые поля в JSON
        ObjectNode modifiedBody = ((ObjectNode) rawJsonBody);
        return exchange
            .getPrincipal()
            .flatMap(princiapal -> {
                modifiedBody.put("username", princiapal.getName());
                return webClient
                    .method(exchange.getRequest().getMethod())
                    .uri("/api" + exchange.getRequest().getPath().subPath(4))
                    .bodyValue(modifiedBody)
                    .retrieve()
                    .toEntity(String.class);
            });
    }
}
