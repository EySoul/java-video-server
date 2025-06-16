package com.project_nine.userway;

import static org.assertj.core.api.Assertions.assertThat;

import com.project_nine.userway.config.AuthManager;
import com.project_nine.userway.config.CorsConfig;
import com.project_nine.userway.config.SecurityContextRespository;
import com.project_nine.userway.config.TestSecurityConfig;
import com.project_nine.userway.config.TestWebClientConfig;
import com.project_nine.userway.controller.SocialController;
import com.project_nine.userway.controller.VideoProxyController;
import com.project_nine.userway.repository.UserRepository;
import com.project_nine.userway.service.JwtService;
import com.project_nine.userway.service.MetricsService;
import com.project_nine.userway.service.UserService;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(
    controllers = {
        VideoProxyController.class, SocialController.class,
        // VideoUploadProxyController.class,
    }
)
@Import(
    {
        TestWebClientConfig.class,
        TestSecurityConfig.class,
        CorsConfig.class,
        SecurityContextRespository.class,
    }
)
@MockBean(
    {
        UserRepository.class,
        UserService.class,
        JwtService.class,
        AuthManager.class,
    }
)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ProxyControllersIntegrationTest {

    private static MockWebServer videoServiceMock;
    private static MockWebServer socialServiceMock;

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    @Qualifier("metricsService")
    private MetricsService metricsService;

    @BeforeAll
    static void setUp() throws IOException {
        videoServiceMock = new MockWebServer();
        socialServiceMock = new MockWebServer();
        videoServiceMock.start(8081);
        socialServiceMock.start(8082);
    }

    @AfterAll
    static void tearDown() throws IOException {
        videoServiceMock.shutdown();
        socialServiceMock.shutdown();
    }

    @Test
    void getVideoById_shouldReturnExpectedData() throws Exception {
        // Настраиваем мок-ответ для конкретного ID
        String expectedResponse = "{\"id\":123,\"title\":\"Test Video\"}";
        videoServiceMock.enqueue(
            new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(expectedResponse)
        );

        // Выполняем запрос
        webTestClient
            .get()
            .uri("/api/video/123")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo(123)
            .jsonPath("$.title")
            .isEqualTo("Test Video");

        // Проверяем запрос
        RecordedRequest request = videoServiceMock.takeRequest();
        assertThat(request.getPath()).isEqualTo("/api/video/123");
        System.out.println("Actual request path: " + request.getPath());
        System.out.println("Request headers: " + request.getHeaders());
    }

    @Test
    void testSocialInteraction() throws Exception {
        String expectedResponse =
            "{\"id\":1,\"username\":\"someUser\",\"video\":\"123\"}";

        socialServiceMock.enqueue(
            new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(expectedResponse)
        );
        System.out.println(
            "START TO TEST testSocialInteraction, Тест для SocialController"
        );

        // Тест для SocialController
        webTestClient
            .post()
            .uri("/api/social/like/123")
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus()
            .isOk();

        // Verify mock server received request
        RecordedRequest request = socialServiceMock.takeRequest(
            3,
            TimeUnit.SECONDS
        );
        assertThat(request).isNotNull();
        assertThat(request.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(
            MediaType.APPLICATION_JSON_VALUE
        );
        assertThat(request.getPath()).isEqualTo("/api/like/123");

    }

    @Test
    void getVideoStream_shouldReturnBinaryData() throws Exception {
        // Подготовка бинарного контента
        byte[] videoBytes = "fake-video-content".getBytes();
        Buffer buffer = new Buffer().write(videoBytes);
        videoServiceMock.enqueue(
            new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "video/mp4")
                .setBody(buffer)
        );

        webTestClient
            .get()
            .uri("/api/videos/456")
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType("video/mp4")
            .expectBody(byte[].class)
            .value(response -> assertThat(response).isEqualTo(videoBytes));

        buffer.close();
    }
}
