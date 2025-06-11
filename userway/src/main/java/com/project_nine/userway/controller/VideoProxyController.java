package com.project_nine.userway.controller;

import com.project_nine.userway.service.MetricsService;
import java.time.Duration;
import java.util.List;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * VideoProxyController
 */
@RestController
public class VideoProxyController {

    private final WebClient webClient;

    private final MetricsService metricsService;

    public VideoProxyController(
        WebClient videoServiceWebClient,
        MetricsService metricsService
    ) {
        this.webClient = videoServiceWebClient;
        this.metricsService = metricsService;
    }

    @GetMapping("/api/videos")
    public Flux<String> getVideos() {
        return webClient
            .get()
            .uri("/api/videos")
            .retrieve()
            .bodyToFlux(String.class);
    }

    @GetMapping("/api/videos/{id}")
    public Mono<String> getVideoById(@PathVariable String id) {
        return webClient
            .get()
            .uri("/api/videos/{id}", id)
            .retrieve()
            .bodyToMono(String.class);
    }

    @GetMapping(value = "/api/videos/**", produces = "video/mp4")
    public Mono<ResponseEntity<Flux<DataBuffer>>> getVideo(
        ServerWebExchange exchange
    ) {
        long startTime = System.nanoTime();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Подготовка заголовков
        HttpHeaders headers = new HttpHeaders();
        request.getHeaders().forEach(headers::addAll);
        headers.remove(HttpHeaders.HOST); // Важно!

        // Определение диапазона
        String rangeHeader = request.getHeaders().getFirst(HttpHeaders.RANGE);
        boolean isPartialRequest =
            rangeHeader != null && rangeHeader.startsWith("bytes=");

        return webClient
            .get()
            .uri(path)
            .headers(h -> h.addAll(headers))
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .retrieve()
            .toEntityFlux(DataBuffer.class)
            .map(responseEntity -> {
                // Копируем все заголовки ответа
                HttpHeaders responseHeaders = new HttpHeaders();
                responseEntity
                    .getHeaders()
                    .forEach((key, values) -> {
                        // Убираем конфликтующие заголовки
                        if (!key.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                            responseHeaders.addAll(key, values);
                        }
                    });

                // Критически важные настройки
                responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
                responseHeaders.setAccessControlAllowOrigin("*");
                responseHeaders.setAccessControlExposeHeaders(
                    List.of("Content-Range", "Accept-Ranges")
                );

                // Устанавливаем правильный статус
                HttpStatus status = isPartialRequest
                    ? HttpStatus.PARTIAL_CONTENT
                    : HttpStatus.OK;
                metricsService.recordRequest(
                    Duration.ofNanos(System.nanoTime() - startTime)
                );
                return ResponseEntity.status(status)
                    .headers(responseHeaders)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .body(responseEntity.getBody());
            });
    }
}
