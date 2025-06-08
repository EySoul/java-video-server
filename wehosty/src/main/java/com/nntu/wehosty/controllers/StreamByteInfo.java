package com.nntu.wehosty.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@AllArgsConstructor
@Data
public class StreamByteInfo {

    private final StreamingResponseBody responseBody;
    private final Long fileSize; // Полный размер файла
    private final Long start; // Начало диапазона
    private final Long end; // Конец диапазона

    // Длина контента для текущего диапазона
    public long getContentLength() {
        return end - start + 1;
    }

    // Полный размер файла (для Content-Range)
    public long getFullSize() {
        return fileSize;
    }
}
