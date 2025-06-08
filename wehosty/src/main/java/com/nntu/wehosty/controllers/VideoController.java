package com.nntu.wehosty.controllers;

import com.nntu.wehosty.exceptions.StorageException;
import com.nntu.wehosty.services.StorageService;
import com.nntu.wehosty.services.VideoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
public class VideoController {

    // private ResourceLoader resourceLoader;

    @Autowired
    private VideoService videoService;

    @Autowired
    private final StorageService storageService;

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
    public String fileUpload(
        @RequestParam("file") MultipartFile file,
        @RequestParam("image") MultipartFile image,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("author") String nameString
    ) {
        try {
            storageService.store(file, nameString, title, description, image);
        } catch (StorageException e) {
            System.out.println("Не получилось" + e);
            return "NONONO";
        }
        System.out.println("Получилось");
        return "yesyesyes";
    }
}
