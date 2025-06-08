package com.project_nine.userway.config;

import org.springframework.context.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.*;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        return new CorsWebFilter(exchange -> {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("*");
            config.addAllowedHeader("*");
            config.addAllowedMethod("GET");
            config.addExposedHeader("Content-Range");
            config.addExposedHeader("Accept-Ranges");
            config.addExposedHeader("Content-Type");
            return config;
        });
    }
}
