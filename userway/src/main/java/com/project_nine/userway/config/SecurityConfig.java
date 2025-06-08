package com.project_nine.userway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // private final JwtAuthWebFilter jwtAuthenticationFilter;
    // private final UserService userService;

    private final AuthManager authManager;
    private final SecurityContextRespository scr;

    public SecurityConfig(
        AuthManager authManager,
        SecurityContextRespository contextRespository
    ) {
        this.authManager = authManager;
        this.scr = contextRespository;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
        ServerHttpSecurity http
    ) {
        return http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(t ->
                t
                    .authenticationEntryPoint((swe, e) ->
                        Mono.fromRunnable(() ->
                            swe
                                .getResponse()
                                .setStatusCode(HttpStatus.UNAUTHORIZED)
                        )
                    )
                    .accessDeniedHandler((swe, e) ->
                        Mono.fromRunnable(() ->
                            swe
                                .getResponse()
                                .setStatusCode(HttpStatus.FORBIDDEN)
                        )
                    )
            )
            .authorizeExchange(exchanges ->
                exchanges
                    .pathMatchers("/api/videos/upload")
                    .authenticated()
                    .pathMatchers(
                        "/api/videos/*",
                        "/api/registration",
                        "/api/login",
                        "/actuator/**"
                    )
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            )
            .httpBasic(t -> t.disable())
            .authenticationManager(authManager)
            .securityContextRepository(scr)
            .formLogin(t -> t.disable())
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // @Bean
    //  public ReactiveAuthenticationManager authenticationManager(
    //      ReactiveUserDetailsService userDetailsService,
    //      PasswordEncoder passwordEncoder
    //  ) {
    //      var provider = new DaoReactiveAuthenticationProvider(userDetailsService, passwordEncoder);
    //      return new ProviderReactiveAuthenticationManager(provider);
    //  }
}
