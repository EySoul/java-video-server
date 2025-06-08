package com.project7.social_service.Repository;

import com.project7.social_service.Model.Entty.Comment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CommentRepository extends R2dbcRepository<Comment, Long> {
    Flux<Comment> findByVideo(long video_id);
    Flux<Comment> findByParent(long parent_id);
}
