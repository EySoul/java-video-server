package com.project7.social_service.Controller;

// import com.project7.social_service.Model.DTO.CommentRequestDTO;
import com.project7.social_service.Model.Entty.Comment;
import com.project7.social_service.Service.CommentService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping("/add")
    @ResponseBody
    public Mono<Comment> add(@RequestBody Mono<Comment> commentDtoMono) {
        return commentDtoMono.flatMap(comDTO ->
            service.save(Comment.build(comDTO))
        );
    }

    @GetMapping("/delete/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable Long id) {
        return service
            .getById(id)
            .flatMap(service::delete)
            .thenReturn(ResponseEntity.ok().build())
            .onErrorResume(DataAccessException.class, e ->
                Mono.just(ResponseEntity.badRequest().build())
            );
    }

    @GetMapping("/video/{id}")
    public Flux<Comment> getAllCommentsById(@PathVariable Long id) {
        return service.getAllByVideoId(Mono.just(id));
    }

    @GetMapping("/child/{id}")
    public Flux<Comment> getChildren(@PathVariable Long id) {
        return service.getChildren(Mono.just(id));
    }

    @PostMapping("/update")
    @ResponseBody
    public Mono<ResponseEntity<Comment>> update(
        @RequestBody Mono<Comment> commentDtoMono
    ) {
        return commentDtoMono.flatMap(dto -> {
            return service
                .update(dto)
                .map(comment -> ResponseEntity.ok(comment));
        });
    }
}
