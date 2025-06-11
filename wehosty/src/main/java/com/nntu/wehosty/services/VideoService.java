package com.nntu.wehosty.services;

import com.nntu.wehosty.models.StreamByteInfo;
import com.nntu.wehosty.models.VideoData;
import com.nntu.wehosty.repositories.VideoRepository;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    @Autowired
    private final VideoRepository videoRepository;

    public void UploadVideo(VideoData video) {
        log.info("saving video data {}", video);
        videoRepository.save(video);
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

    public Optional<StreamByteInfo> getSreamBytes(
        String uuid,
        HttpRange range
    ) {
        String videoPathSt = "./videos/" + uuid;
        Path videoPath = Path.of(videoPathSt);
        File videoFile = new File(videoPathSt);

        if (!videoFile.exists()) {
            return Optional.empty();
        }
        try {
            long fileSize = Files.size(videoPath);
            long chunkSize = 2000000;
            if (range == null) {
                return Optional.of(
                    new StreamByteInfo(
                        out -> Files.newInputStream(videoPath).transferTo(out),
                        fileSize,
                        0L,
                        chunkSize
                    )
                );
            }
            long rangeStart = range.getRangeStart(0);
            long rangeEnd = rangeStart + chunkSize;
            if (rangeEnd >= fileSize) {
                rangeEnd = fileSize - 1;
            }
            long finalRangeEnd = rangeEnd;
            return Optional.of(
                new StreamByteInfo(
                    outputStream -> {
                        try (InputStream is = Files.newInputStream(videoPath)) {
                            is.skip(rangeStart);
                            byte[] bytes = is.readNBytes(
                                ((int) (finalRangeEnd - rangeStart) + 1)
                            );
                            outputStream.write(bytes);
                        }
                    },
                    fileSize,
                    rangeStart,
                    finalRangeEnd
                )
            );
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
