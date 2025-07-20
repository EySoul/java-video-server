package com.nntu.wehosty.services;

import com.nntu.wehosty.controllers.VideoController.UploadRequest;
import com.nntu.wehosty.models.StreamByteInfo;
import com.nntu.wehosty.models.VideoData;
import com.nntu.wehosty.repositories.VideoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final StorageService storageService;

    public VideoService(
        VideoRepository videoRepository,
        StorageService storageService
    ) {
        this.storageService = storageService;
        this.videoRepository = videoRepository;
    }

    public String UploadVideo(UploadRequest video) throws IOException {
        log.info("saving video data {}", video);
        UUID uuid = storageService.store(video.videoFile(), video.imageFile());
        return videoRepository
            .save(
                new VideoData(
                    uuid.toString(),
                    video.title(),
                    video.description(),
                    video.author()
                )
            )
            .getVideo_uuid();
    }

    public void DeleteVideo(Long id) {
        videoRepository.deleteById(id);
    }

    public List<VideoData> getVideos() {
        return videoRepository.findAll();
    }

    public VideoData getVideoById(Long id) {
        return videoRepository.findById(id).orElse(null);
    }

    public Long getLastVideoId() {
        return videoRepository.count();
    }

    public Optional<StreamByteInfo> getBytes(String uuid, HttpRange range) {
        return storageService.getStreamBytes(uuid, range);
    }
}
