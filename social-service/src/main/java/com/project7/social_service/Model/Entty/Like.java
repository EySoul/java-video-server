package com.project7.social_service.Model.Entty;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@Table("likes")
@NoArgsConstructor
public class Like {

    @Id
    private Long id;

    private LikeType type;
    private String username;
    private Long video;
    private Long comment;
    private LocalDateTime created_at;

    public Like(LikeType type, String user, Long video, Long comment) {
        this.type = type;
        this.username = user;
        this.video = video;
        this.created_at = LocalDateTime.now();
        this.comment = comment;
    }

    public Boolean isComment() {
        return this.getComment() == null ? false : true;
    }

    public Like build() {
        System.out.println("Пришел на сборку такой лайк -- " + this);
        return new Like(this.type, this.username, this.video, this.comment);
    }

    public Like switchType(LikeType type) {
        this.type = type;
        System.out.println("Смена!!!!!!");
        return this;
    }
}
