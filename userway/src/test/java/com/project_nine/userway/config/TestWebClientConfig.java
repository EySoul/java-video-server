package com.project_nine.userway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("test")
public class TestWebClientConfig {

    @Bean("videoServiceWebClient")
    @Qualifier("videoServiceWebClient")
    public WebClient videoServiceWebClient() {
        return WebClient.builder().baseUrl("http://localhost:8081").build();
    }

    @Bean("socialWebClient")
    @Qualifier("socialWebClient")
    public WebClient socialWebClient() {
        return WebClient.builder().baseUrl("http://localhost:8082").build();
    }
}
