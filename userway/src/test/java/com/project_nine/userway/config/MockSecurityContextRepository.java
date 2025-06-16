package com.project_nine.userway.config;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class MockSecurityContextRepository
    implements ServerSecurityContextRepository {

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.just(
            new SecurityContextImpl(
                new TestingAuthenticationToken("testUser", null, "ROLE_USER")
            )
        );
    }

    @Override
    public Mono<Void> save(
        ServerWebExchange exchange,
        SecurityContext context
    ) {
        return Mono.empty();
    }
}
