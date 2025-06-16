package com.project7.social_service.Controller;

import com.project7.social_service.Model.DTO.LikeResponseDTO;
import com.project7.social_service.Model.Entty.Like;
import com.project7.social_service.Service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * LikeController
 */
@RestController
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService service;

    public LikeController(LikeService service) {
        this.service = service;
    }

    @PostMapping("/add")
    @ResponseBody
    public Mono<ResponseEntity<LikeResponseDTO>> add(
        @RequestBody Mono<Like> likeMono
    ) {
        return likeMono
            .flatMap(like -> {
                System.out.println(like);
                if (like.isComment()) {
                    return service
                        .findByUsernameAndComment(like)
                        .flatMap(exist ->
                            service
                                .update(exist, like.getType())
                                .map(t ->
                                    ResponseEntity.accepted()
                                        .body(LikeResponseDTO.fromLike(t))
                                )
                        )
                        .switchIfEmpty(
                            service
                                .save(like)
                                .map(l ->
                                    ResponseEntity.ok(
                                        LikeResponseDTO.fromLike(l)
                                    )
                                )
                        );
                }
                return service
                    .findByUsernameAndVideoIdNoComment(like)
                    .flatMap(exist ->
                        service
                            .update(exist, like.getType())
                            .map(t ->
                                ResponseEntity.accepted()
                                    .body(LikeResponseDTO.fromLike(t))
                            )
                    )
                    .switchIfEmpty(
                        service
                            .save(like)
                            .map(e ->
                                ResponseEntity.ok(LikeResponseDTO.fromLike(e))
                            )
                    );
            })
            .onErrorResume(e -> {
                System.err.println("Ошибка: " + e.getMessage());
                return Mono.just(ResponseEntity.badRequest().build());
            });
    }

    @PostMapping("/delete")
    public Mono<ResponseEntity<LikeResponseDTO>> delete(
        @RequestBody Mono<Like> likeMono
    ) {
        return likeMono
            .flatMap(like -> {
                if (like.isComment()) {
                    return service
                        .findByUsernameAndComment(like)
                        .flatMap(service::delete)
                        .thenReturn(
                            ResponseEntity.accepted()
                                .body(LikeResponseDTO.fromLike(like))
                        );
                }
                return service
                    .findByUsernameAndVideoIdNoComment(like)
                    .flatMap(service::delete)
                    .thenReturn(
                        ResponseEntity.accepted()
                            .body(LikeResponseDTO.fromLike(like))
                    );
            })
            .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PostMapping("/get")
    public Flux<Like> get(@RequestBody Mono<Like> likeMono) {
        return likeMono.flatMapMany(service::findByUsernameAndVideoId);
    }
}
