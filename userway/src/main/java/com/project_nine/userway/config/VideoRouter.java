// package com.project_nine.userway.config;

// import com.project_nine.userway.handler.VideoHandler;
// import org.springframework.context.annotation.*;
// import org.springframework.web.reactive.function.server.*;

// @Configuration
// public class VideoRouter {

//     @Bean
//     public RouterFunction<ServerResponse> videoRoute(VideoHandler handler) {
//         System.out.println("ПОпал в обработчик");
//         return RouterFunctions.route()
//             .GET("/api/vides/{videoId}", handler::streamVideo)
//             .build();
//     }
// }
