package com.project7.social_service.Model.Entty;

// import com.project7.social_service.Model.DTO.CommentRequestDTO;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@Table("comment")
public class Comment {

    @Id
    private Long id;

    private String text;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String username;
    private Long video;
    private Long parent;

    public Comment() {}

    // @PersistenceCreator
    public Comment(Long id, String text, String username, long video) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.video = video;
    }

    public Comment(String text, String user_id, long video_id) {
        this.text = text;
        this.username = user_id;
        this.video = video_id;
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }

    public Comment(String text, String user_id, long video_id, Long parent) {
        this.text = text;
        this.username = user_id;
        this.video = video_id;
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
        this.parent = parent;
    }

    public static Comment build(Comment dto) {
        return new Comment(
            dto.getText(),
            dto.getUsername(),
            dto.getVideo(),
            dto.getParent()
        );
    }

    public Comment updateFromDto(Comment dto) {
        this.text = dto.getText();
        return this;
    }
}
