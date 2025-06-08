// package com.project_nine.userway.controller;

// import java.util.ArrayList;
// import org.springframework.core.io.buffer.DataBuffer;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpStatusCode;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.http.server.reactive.ServerHttpResponse;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.server.ServerWebExchange;
// import reactor.core.publisher.Mono;

// @RestController
// public class DefaultProxyController {

//     private final WebClient webClient;

//     public DefaultProxyController(WebClient.Builder webClientBuilder) {
//         this.webClient = webClientBuilder
//             .baseUrl("http://video-service")
//             .build();
//     }

//     @RequestMapping("/api/video/**")
//     public Mono<Void> proxy(ServerWebExchange exchange) {
//         ServerHttpRequest request = exchange.getRequest();
//         HttpMethod method = request.getMethod();
//         String path = getPath(exchange);

//         WebClient.RequestBodySpec bodySpec = webClient
//             .method(method)
//             .uri(uriBuilder ->
//                 uriBuilder.path(path).query(request.getURI().getQuery()).build()
//             )
//         ;

//         WebClient.RequestHeadersSpec<?> headersSpec = copyHeaders(
//             request,
//             bodySpec
//         )
//         ;

//         return headersSpec

//             .retrieve()
//             .onStatus(HttpStatusCode::isError, clientResponse ->
//                 Mono.error(new RuntimeException("Error from video service"))
//             )
//             .toEntity(DataBuffer.class)
//             .flatMap(response -> {
//                 ServerHttpResponse proxyResponse = exchange.getResponse();
//                 proxyResponse.setStatusCode(response.getStatusCode());

//                 // Копируем заголовки
//                 proxyResponse
//                     .getHeaders()
//                     .putAll(copySafeHeaders(response.getHeaders()));

//                 // Отдаём тело как DataBuffer
//                 return proxyResponse.writeWith(Mono.just(response.getBody()));
//             });
//     }

//     private String getPath(ServerWebExchange exchange) {
//         String path = exchange.getRequest().getPath().value();
//         return path.replaceFirst("/api/video", "");
//     }

//     private WebClient.RequestHeadersSpec<?> copyHeaders(
//         ServerHttpRequest from,
//         WebClient.RequestBodySpec to
//     ) {
//         from
//             .getHeaders()
//             .forEach((name, values) -> {
//                 if (!name.equalsIgnoreCase("host")) {
//                     to.header(name, String.join(",", values));
//                 }
//             });
//         return to;
//     }

//     private HttpHeaders copySafeHeaders(HttpHeaders from) {
//         HttpHeaders safe = new HttpHeaders();
//         from.forEach((key, value) -> {
//             if (
//                 !key.equalsIgnoreCase("transfer-encoding") &&
//                 !key.equalsIgnoreCase("content-length")
//             ) {
//                 safe.put(key, new ArrayList<>(value));
//             }
//         });
//         return safe;
//     }
// }
