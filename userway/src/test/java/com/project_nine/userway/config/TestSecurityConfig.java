package com.project_nine.userway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

@Configuration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityWebFilterChain securityWebFilterChain(
        ServerHttpSecurity http
    ) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex.anyExchange().permitAll())
            .securityContextRepository(securityContextRepository())
            .build();
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new MockSecurityContextRepository();
    }
}
