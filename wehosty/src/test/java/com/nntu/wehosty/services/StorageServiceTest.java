package com.nntu.wehosty.services;

import static org.junit.jupiter.api.Assertions.*;

import com.nntu.wehosty.exceptions.StorageException;
import com.nntu.wehosty.models.StreamByteInfo;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpRange;
import org.springframework.mock.web.MockMultipartFile;

class StorageServiceTest {

    @TempDir
    Path tempDir;

    private StorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new StorageService(tempDir);
    }

    @Test
    void storeHappyPath() throws Exception {
        MockMultipartFile video = new MockMultipartFile(
            "video",
            "test.mp4",
            "video/mp4",
            "fake video bytes".getBytes()
        );
        MockMultipartFile image = new MockMultipartFile(
            "image",
            "thumb.jpg",
            "image/jpeg",
            "fake image bytes".getBytes()
        );

        var uuid = storageService.store(video, image);

        // Both files should exist
        assertTrue(Files.exists(tempDir.resolve(uuid.toString())));
        assertTrue(
            Files.exists(tempDir.resolve("imgs").resolve(uuid + ".jpg"))
        );
    }

    @Test
    void storeThrowsWhenVideoIsEmpty() {
        MockMultipartFile emptyVideo = new MockMultipartFile(
            "video",
            "empty.mp4",
            "video/mp4",
            new byte[0]
        );
        MockMultipartFile image = new MockMultipartFile(
            "image",
            "thumb.jpg",
            "image/jpeg",
            "fake image bytes".getBytes()
        );

        StorageException ex = assertThrows(StorageException.class, () ->
            storageService.store(emptyVideo, image)
        );
        System.out.println(ex.getMessage());
        assertTrue(ex.getMessage().contains("empty file"));
    }

    @Test
    void storeThrowsWhenImageIsEmpty() {
        MockMultipartFile video = new MockMultipartFile(
            "video",
            "test.mp4",
            "video/mp4",
            "fake video bytes".getBytes()
        );
        MockMultipartFile emptyImage = new MockMultipartFile(
            "image",
            "thumb.jpg",
            "image/jpeg",
            new byte[0]
        );

        StorageException ex = assertThrows(StorageException.class, () ->
            storageService.store(video, emptyImage)
        );
        System.out.println(ex.getMessage());

        assertTrue(ex.getMessage().contains("empty file"));
    }

    @Test
    void getStreamBytesNonExistingFile() {
        Optional<StreamByteInfo> result = storageService.getStreamBytes(
            "non_existing.xyz",
            null
        );
        assertTrue(result.isEmpty());
    }

    @Test
    void getStreamBytesWithRange() throws Exception {
        // Подготовка тестовых данных
        byte[] testData = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes();
        Path testFile = tempDir.resolve("range_test.txt");
        Files.write(testFile, testData);

        // Запрашиваем диапазон 5-9 байт (F-J)
        HttpRange range = HttpRange.createByteRange(5, 9);
        Optional<StreamByteInfo> result = storageService.getStreamBytes(
            "range_test.txt",
            range
        );

        assertTrue(result.isPresent());
        StreamByteInfo info = result.get();

        // Проверяем метаданные
        assertEquals(testData.length, info.getFileSize());
        assertEquals(5L, info.getStart());
        assertEquals(9L, info.getEnd());

        // Проверяем полученные данные
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        info.getResponseBody().writeTo(bos);
        assertArrayEquals(
            Arrays.copyOfRange(testData, 5, 9),
            bos.toByteArray()
        );
    }
}
