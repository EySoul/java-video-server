package com.project7.social_service.Service;

import com.project7.social_service.Model.Entty.Comment;
import com.project7.social_service.Repository.CommentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CommentService
 */
@Service
public class CommentService {

    private final CommentRepository repository;

    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    public Mono<Comment> save(Mono<Comment> commentMono) {
        return commentMono.flatMap(comment -> repository.save(comment));
    }

    public Mono<Comment> save(Comment comment) {
        return repository.save(comment);
    }

    public Mono<Comment> update(Comment comment) {
        return repository
            .findById(comment.getId())
            .flatMap(exist -> repository.save(exist.updateFromDto(comment)))
            .switchIfEmpty(Mono.empty());
    }

    public Mono<Void> delete(Comment commentMono) {
        return repository.delete(commentMono);
    }

    public Mono<Comment> getById(Long id) {
        return repository.findById(id);
    }

    public Flux<Comment> getAllByVideoId(Mono<Long> video_id) {
        return video_id.flatMapMany(id -> repository.findByVideo(id));
    }

    public Flux<Comment> getChildren(Mono<Long> parent_id) {
        return parent_id.flatMapMany(id -> repository.findByParent(id));
    }
}
