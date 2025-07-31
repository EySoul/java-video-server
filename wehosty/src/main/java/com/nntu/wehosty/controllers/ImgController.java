// package com.nntu.wehosty.controllers;
// import com.nntu.wehosty.repositories.ImageRepository;
// import java.io.File;
// import java.nio.file.Path;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.ResourceLoader;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// // TODO
// // Обработка картинки в storageService
// // Избавиться от Entity image, нет смысла так как картинка существует когда существует видео.
// // Обрать обработку в ImageService
// @RestController
// public class ImgController {
//     @Autowired
//     ImageRepository imageRepository;
//     @Autowired
//     private ResourceLoader resourceLoader;
//     private final String rootLocation = "./videos/imgs";
//     private Path mainPath = Path.of(rootLocation);
//     @GetMapping("/api/img")
//     public ResponseEntity<Resource> getImg(@RequestParam("id") String imgid) {
//         if (imageRepository.findById(Long.parseLong(imgid)).isEmpty()) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//         }
//         Path imagePath = mainPath.resolve(imgid + ".jpg");
//         File file = new File(imagePath.toString());
//         if (!file.exists()) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//         }
//         Resource resource = resourceLoader.getResource(
//             "file:" + file.getAbsolutePath()
//         );
//         HttpHeaders headers = new HttpHeaders();
//         headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");
//         return new ResponseEntity<>(resource, headers, HttpStatus.OK);
//     }
// }
