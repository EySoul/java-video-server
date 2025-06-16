// package com.project_nine.userway.config;

// import java.io.IOException;
// import okhttp3.mockwebserver.MockWebServer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Profile;

// @Configuration
// @Profile("test")
// public class MockSeverConfig {

//     @Bean(name = "socialServiceMock", destroyMethod = "shutdown")
//     public MockWebServer socialServiceMock() throws IOException {
//         MockWebServer server = new MockWebServer();
//         server.start(8082);
//         return server;
//     }

//     @Bean(name = "videoServiceMock", destroyMethod = "shutdown")
//     public MockWebServer videoServiceMock() throws IOException {
//         MockWebServer server = new MockWebServer();
//         server.start(8081);
//         return server;
//     }
// }
