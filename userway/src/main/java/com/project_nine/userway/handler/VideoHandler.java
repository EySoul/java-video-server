// package com.project_nine.userway.handler;

// import org.springframework.core.io.buffer.DataBuffer;
// import org.springframework.http.*;
// import org.springframework.stereotype.*;
// import org.springframework.web.reactive.function.BodyInserters;
// import org.springframework.web.reactive.function.client.*;
// import org.springframework.web.reactive.function.server.*;
// import reactor.core.publisher.*;

// @Component
// public class VideoHandler {

//     private final WebClient videoServiceWebClient;

//     public VideoHandler(WebClient videoServiceWebClient) {
//         this.videoServiceWebClient = videoServiceWebClient;
//     }

//     public Mono<ServerResponse> streamVideo(ServerRequest request) {
//         String videoId = request.pathVariable("videoId");

//         String rangeHeader = request
//             .headers()
//             .header(HttpHeaders.RANGE)
//             .stream()
//             .findFirst()
//             .orElse(null);

//         return videoServiceWebClient
//             .get()
//             .uri("/videos/{id}", videoId)
//             .headers(clientHeaders -> {
//                 if (rangeHeader != null) {
//                     clientHeaders.set(HttpHeaders.RANGE, rangeHeader);
//                 }
//             })
//             .exchangeToMono(response -> {
//                 if (!response.statusCode().is2xxSuccessful()) {
//                     return response
//                         .createException()
//                         .flatMap(ex ->
//                             ServerResponse.status(
//                                 HttpStatus.BAD_GATEWAY
//                             ).build()
//                         );
//                 }

//                 // Проксируем заголовки
//                 HttpHeaders headers = new HttpHeaders();
//                 response
//                     .headers()
//                     .asHttpHeaders()
//                     .forEach((key, values) -> headers.put(key, values));

//                 // Важно: устанавливаем Content-Length, если есть
//                 Long contentLength = response
//                     .headers()
//                     .contentLength()
//                     .orElse(-1L);
//                 if (contentLength > 0) {
//                     headers.setContentLength(contentLength);
//                 }

//                 Flux<DataBuffer> body = response.bodyToFlux(DataBuffer.class);

//                 return ServerResponse.status(HttpStatus.PARTIAL_CONTENT)
//                     .headers(h -> h.addAll(headers))
//                     .contentType(MediaType.valueOf("video/mp4"))
//                     .body(BodyInserters.fromDataBuffers(body));
//             });
//     }
// }
