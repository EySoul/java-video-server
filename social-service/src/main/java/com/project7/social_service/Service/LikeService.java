package com.project7.social_service.Service;

import com.project7.social_service.Model.Entty.Like;
import com.project7.social_service.Model.Entty.LikeType;
import com.project7.social_service.Repository.LikeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * LikeService
 */
@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public Mono<Like> save(Mono<Like> likeMono) {
        return likeMono.flatMap(like -> likeRepository.save(like));
    }

    public Mono<Like> save(Like like) {
        System.out.println("Пришел такой лайк -- " + like);
        return likeRepository.save(like.build());
    }

    public Mono<Like> update(Like like, LikeType type) {
        return likeRepository.save(like.switchType(type));
    }


    public Mono<Like> findByUsernameAndVideoIdNoComment(Like like) {
        return likeRepository.findByUsernameAndVideoAndComment(
            like.getUsername(),
            like.getVideo(),
            like.getComment()
        );
    }

    public Flux<Like> findByUsernameAndVideoId(Like like) {
        return likeRepository.findByUsernameAndVideo(
            like.getUsername(),
            like.getVideo()
        );
    }

    public Mono<Like> findByUsernameAndComment(Like like) {
        return likeRepository.findByUsernameAndComment(
            like.getUsername(),
            like.getComment()
        );
    }

    public Mono<Void> delete(Like like) {
        return likeRepository.delete(like);
    }
}
