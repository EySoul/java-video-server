package com.project_nine.userway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class VideoUploadProxyController {

    private final WebClient webClient;

    public VideoUploadProxyController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://video-service:8081")
            .build();
    }

    @PostMapping(
        value = "/api/videos/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Mono<String> uploadVideo(
        @RequestPart("file") FilePart videoFile,
        @RequestPart("image") FilePart imageFile,
        @RequestPart("title") String title,
        @RequestPart("description") String description,
        ServerWebExchange exchange
    ) {
        return exchange
            .getPrincipal()
            .flatMap(principl -> {
                // Собираем мультипарт запрос
                MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

                // Добавляем файлы и данные
                bodyBuilder
                    .part("file", videoFile)
                    .filename(videoFile.filename())
                    .contentType(
                        MediaType.parseMediaType(
                            videoFile.headers().getContentType().toString()
                        )
                    );

                bodyBuilder
                    .part("image", imageFile)
                    .filename(imageFile.filename())
                    .contentType(
                        MediaType.parseMediaType(
                            imageFile.headers().getContentType().toString()
                        )
                    );

                bodyBuilder.part("title", title);
                bodyBuilder.part("description", description);
                bodyBuilder.part("author", principl.getName());

                return webClient
                    .post()
                    .uri("/videos/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .bodyToMono(String.class);
            });
    }
}
