package com.nntu.wehosty.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "video")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Id;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "author")
    private String author;

    @Column(name = "video_uuid")
    private String video_uuid;

    @Column(name = "date_add")
    private LocalDateTime date_add;

    @Column(name = "date_update")
    private LocalDateTime date_update;

    @PrePersist
    private void init() {
        date_add = LocalDateTime.now();
        date_update = date_add;
    }

    public VideoData(
        String uuid,
        String title,
        String description,
        String author
    ) {
        this.video_uuid = uuid;
        this.title = title;
        this.description = description;
        this.author = author;
    }
}
