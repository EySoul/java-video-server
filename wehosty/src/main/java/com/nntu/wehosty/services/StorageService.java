package com.nntu.wehosty.services;

import com.nntu.wehosty.exceptions.StorageException;
import com.nntu.wehosty.models.StreamByteInfo;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Service
public class StorageService {

    private final Path rootLocation;
    private long defaultChunkSize = 2_000_000;

    // private static final Logger log = Logger.getLogger(StorageService.class.getName());

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

    public Optional<StreamByteInfo> getStreamBytes(
        String uuid,
        HttpRange range
    ) {
        Path videoPath = this.rootLocation.resolve(uuid);
        try {
            long fileSize = Files.size(videoPath);
            StreamingResponseBody body = outputStream -> {
                try (
                    FileChannel channel = FileChannel.open(
                        videoPath,
                        StandardOpenOption.READ
                    );
                    WritableByteChannel outChannel = Channels.newChannel(
                        outputStream
                    );
                ) {
                    if (range == null) {
                        channel.transferTo(0, fileSize, outChannel);
                    } else {
                        long rangeStart = range.getRangeStart(0);
                        long rangeEnd = Math.min(
                            range.getRangeEnd(fileSize),
                            rangeStart + defaultChunkSize
                        );
                        rangeEnd = Math.min(rangeEnd, fileSize - 1);
                        channel.transferTo(
                            rangeStart,
                            rangeEnd - rangeStart,
                            outChannel
                        );
                    }
                }
            };
            long rangeStart = range != null ? range.getRangeStart(0) : 0;
            long rangeEnd = range != null
                ? Math.min(
                    range.getRangeEnd(fileSize),
                    rangeStart + defaultChunkSize
                )
                : Math.min(defaultChunkSize, fileSize) - 1;
            return Optional.of(
                new StreamByteInfo(body, fileSize, rangeStart, rangeEnd)
            );
        } catch (IOException ex) {
            return Optional.empty();
        }
    }
}
