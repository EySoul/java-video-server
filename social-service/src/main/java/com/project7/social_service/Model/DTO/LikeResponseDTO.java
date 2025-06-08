package com.project7.social_service.Model.DTO;

import com.project7.social_service.Model.Entty.Like;
import com.project7.social_service.Model.Entty.LikeType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * LikeResponseDTO
 */
@Data
@AllArgsConstructor
public class LikeResponseDTO {

    private String username;
    private LikeType type;
    private Long video_id;
    private Long comment_id;

    public static LikeResponseDTO fromLike(Like like) {
        return new LikeResponseDTO(
            like.getUsername(),
            like.getType(),
            like.getVideo(),
            like.getComment()
        );
    }
}
