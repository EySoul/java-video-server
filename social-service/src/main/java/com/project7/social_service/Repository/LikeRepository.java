package com.project7.social_service.Repository;

import com.project7.social_service.Model.Entty.Like;
// import java.util.List;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LikeRepository extends ReactiveCrudRepository<Like, Long> {
    Mono<Like> findByUsernameAndVideoAndComment(
        String username,
        Long video,
        Long comment
    );
    Flux<Like> findByUsernameAndVideo(String username, Long video);
    Mono<Like> findByUsernameAndComment(String username, long comment);
    Mono<Like> save(Like like);
}

