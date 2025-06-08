package com.nntu.wehosty.services;

import com.nntu.wehosty.models.Image;
import com.nntu.wehosty.repositories.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ImageService {

    @Autowired
    private final ImageRepository imageRepository;

    public long getLastId() {
        return imageRepository.count();
    }

    public void save(final Image image) {
        imageRepository.save(image);
    }
}
