package com.project_nine.userway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClientConfig
 */
@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("videoServiceWebClient")
    public WebClient videoServiceWebClient() {
        return WebClient.builder().baseUrl("http://video-service:8081").build();
    }

    @Bean
    @Qualifier("socialWebClient")
    public WebClient socialWebClient() {
        return WebClient.builder()
            .baseUrl("http://social-service:8082")
            .build();
    }
}
