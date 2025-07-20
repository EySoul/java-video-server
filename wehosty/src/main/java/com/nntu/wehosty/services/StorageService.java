package com.nntu.wehosty.services;

import com.nntu.wehosty.exceptions.StorageException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    private final Path rootLocation;

    public StorageService(Path root) {
        this.rootLocation = root;
    }

    public StorageService() {
        this.rootLocation = Paths.get("./videos");
    }

    public UUID store(MultipartFile videoFile, MultipartFile imageFile)
        throws IOException {
        try {
            if (videoFile.isEmpty() || imageFile.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            UUID uuid = UUID.randomUUID();
            Path videoFilePath = this.rootLocation.resolve(uuid.toString())
                .normalize()
                .toAbsolutePath();

            Path imageFilePath = this.rootLocation.resolve("imgs").resolve(
                uuid.toString() + ".jpg"
            );
            Files.createDirectories(imageFilePath.getParent());
            try {
                videoFile.transferTo(videoFilePath);
                imageFile.transferTo(imageFilePath);
            } catch (IOException e) {
                System.out.println("Cannot store file");
                throw new StorageException(
                    "Cannot store file: no store on disk" + e
                );
            }
            return uuid;
            // saveImage(imageFilePath);
        } catch (StorageException e) {
            throw new StorageException(e.getMessage());
        }
    }
}
