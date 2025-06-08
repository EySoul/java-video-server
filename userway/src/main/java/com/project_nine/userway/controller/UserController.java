package com.project_nine.userway.controller;

import com.project_nine.userway.domain.dto.SignInRequest;
import com.project_nine.userway.domain.dto.SignUpRequest;
import com.project_nine.userway.domain.model.Role;
import com.project_nine.userway.domain.model.User;
import com.project_nine.userway.service.JwtService;
import com.project_nine.userway.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder paEncoder;

    private static final Logger log = LoggerFactory.getLogger(
        UserController.class
    );

    @GetMapping("/api/user/{username}")
    public Mono<ResponseEntity<User>> getUser(@PathVariable String username) {
        return userService
            .getByUsername(username)
            .cast(User.class)
            .map(user -> {
                log.info("User data retrieved: {}", username);
                return ResponseEntity.ok(user);
            })
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(e -> {
                log.error("Error retrieving user data: {}", username, e);
                return Mono.just(
                    ResponseEntity.status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ).build()
                );
            });
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/api/login")
    public Mono<ResponseEntity> login(
        @RequestBody Mono<SignInRequest> request,
        ServerWebExchange exchange
    ) {
        return request.flatMap(loginRequest ->
            userService
                .getByUsername(loginRequest.getUsername())
                .cast(User.class)
                .map(userDetails ->
                    paEncoder.matches(
                            loginRequest.getPassword(),
                            userDetails.getPassword()
                        )
                        ? ResponseEntity.ok(
                            jwtService.generateToken(userDetails)
                        )
                        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                )
                .defaultIfEmpty(
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
                )
        );
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/api/registration")
    public Mono<ResponseEntity> registration(
        @RequestBody SignUpRequest regRequest
    ) {
        return userService
            .create(
                Mono.just(
                    new User(
                        null,
                        regRequest.getUsername(),
                        paEncoder.encode(regRequest.getPassword()),
                        regRequest.getEmail(),
                        Role.ROLE_USER
                    )
                )
            )
            .flatMap((User u) -> {
                return u != null
                    ? Mono.just(ResponseEntity.ok(jwtService.generateToken(u)))
                    : Mono.just(
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
                    );
            });
    }
}
