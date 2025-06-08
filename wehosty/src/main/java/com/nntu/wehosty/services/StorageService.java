package com.nntu.wehosty.services;

import com.nntu.wehosty.exceptions.StorageException;
import com.nntu.wehosty.models.Image;
import com.nntu.wehosty.models.VideoData;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    private Path rootLocation = Paths.get("./videos");

    @Autowired
    private VideoService videoService;

    @Autowired
    private ImageService imageService;

    public void store(
        MultipartFile file,
        String author,
        String title,
        String description,
        MultipartFile image
    ) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            // if (!isMp4File(file)) {
            //     System.out.println(file.getContentType());
            //     throw new StorageException("Fail to store not mp4 file");
            // }
            String uuid = UUID.randomUUID().toString();
            System.out.println("Create uuid");
            Path destinationFile =
                this.rootLocation.resolve(Paths.get(uuid))
                    .normalize()
                    .toAbsolutePath();

            System.out.println("Create videofile");

            System.out.println("Try to create img file");
            Path imageFile = Path.of(rootLocation + "/imgs/" + uuid + ".jpg");
            System.out.println(imageFile);
            System.out.println(destinationFile);
            System.out.println("imgefile created");
            try {
                System.out.println("Try to store");
                image.transferTo(imageFile);
                file.transferTo(destinationFile);
            } catch (IOException e) {
                System.out.println("Cannot store file");
                throw new StorageException(
                    "Cannot store file: no store on disk"
                );
            }
            System.out.println("stored video and image");
            saveImage(imageFile);
            saveVideoData("/videos/" + uuid, author, title, description, uuid);
        } catch (StorageException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    // private static boolean isMp4File(MultipartFile file) throws IOException {
    //     return "video/mp4".equals(file.getContentType());
    // }

    private void saveVideoData(
        String video_uuid,
        String author,
        String title,
        String description,
        String imageLink
    ) {
        VideoData video = new VideoData();
        video.setVideo_uuid(video_uuid);
        video.setAuthor(author);
        video.setTitle(title);
        video.setDescription(description);
        video.setImage_url(imageLink);
        videoService.UploadVideo(video);
    }

    private void saveImage(Path imageLink) {
        Image im = new Image();
        im.setLink(imageLink.toString());
        imageService.save(im);
    }
}
