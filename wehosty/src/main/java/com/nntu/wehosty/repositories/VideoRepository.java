package com.nntu.wehosty.repositories;

import com.nntu.wehosty.models.VideoData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<VideoData, Long> {
    VideoData getById(Long id);
}
