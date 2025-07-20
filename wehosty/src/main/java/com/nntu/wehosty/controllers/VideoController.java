package com.nntu.wehosty.controllers;

import com.nntu.wehosty.exceptions.StorageException;
import com.nntu.wehosty.models.StreamByteInfo;
import com.nntu.wehosty.services.VideoService;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
public class VideoController {

    private VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping(
        value = "/api/videos/{video}",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<StreamingResponseBody> streamVideo(
        @PathVariable("video") String videoId,
        @RequestHeader(value = "Range", required = false) String range
    ) {
        // Парсинг диапазона
        HttpRange httpRange = null;
        if (range != null && range.startsWith("bytes=")) {
            List<HttpRange> ranges = HttpRange.parseRanges(range);
            if (!ranges.isEmpty()) {
                httpRange = ranges.get(0);
            }
        }

        StreamByteInfo streamByteInfo = videoService
            .getSreamBytes(videoId, httpRange)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Video not found"
                )
            );

        // Определение статуса и заголовков
        boolean isPartial = httpRange != null;
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(
            isPartial ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK
        )
            .contentType(MediaType.parseMediaType("video/mp4"))
            .header("Accept-Ranges", "bytes");

        // Для частичных ответов добавляем Content-Range
        if (isPartial) {
            builder.header(
                "Content-Range",
                "bytes " +
                streamByteInfo.getStart() +
                "-" +
                streamByteInfo.getEnd() +
                "/" +
                streamByteInfo.getFullSize()
            );
        }

        // Устанавливаем Content-Length для всех ответов
        return builder
            .contentLength(streamByteInfo.getContentLength())
            .body(streamByteInfo.getResponseBody());
    }

    @PostMapping("/videos/upload")
    public ResponseEntity<UploadResponse> fileUpload(
        @RequestPart UploadRequest request
    ) throws IOException {
        String uuid;
        try {
            uuid = videoService.UploadVideo(request);
        } catch (StorageException e) {
            System.out.println("Не получилось" + e);
            return ResponseEntity.badRequest().body(
                new UploadResponse("Error: " + e)
            );
        }
        return ResponseEntity.ok().body(
            new UploadResponse(String.format("Saved with uuid: %s", uuid))
        );
    }

    /**
     * @param MPF video
     * @param MPF image
     * @param String title
     * @param String desc
     * @param String author
     */
    public record UploadRequest(
        MultipartFile videoFile,
        MultipartFile imageFile,
        String title,
        String description,
        String author
    ) {}

    private record UploadResponse(String massage) {}
}
