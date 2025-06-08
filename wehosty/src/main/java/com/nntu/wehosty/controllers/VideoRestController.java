package com.nntu.wehosty.controllers;

import com.nntu.wehosty.models.VideoData;
import com.nntu.wehosty.services.VideoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoRestController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/videos")
    public List<VideoData> VideosDelivery() {
        return videoService.getVideos();
    }
}
