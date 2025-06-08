package com.project_nine.userway.service;

import com.project_nine.userway.domain.dto.SignUpRequest;
import com.project_nine.userway.domain.model.Role;
import com.project_nine.userway.domain.model.User;
import com.project_nine.userway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    /**
     * Сохранение пользователя
     */
    public Mono<User> save(Mono<User> userMono) {
        return userMono.flatMap(user -> repository.save(user));
    }

    /**
     * Создание пользователя
     */
    public Mono<User> createFromForm(Mono<SignUpRequest> dataMono) {
        // SignUpRequest data = dataMono.

        return null;
    }

    public Mono<Boolean> existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public Mono<User> create(Mono<User> userMono) {
        return userMono.flatMap(user ->
            repository
                .existsByUsername(user.getUsername())
                .filter(Boolean::booleanValue)
                .flatMap(exists ->
                    Mono.error(
                        new RuntimeException(
                            "Пользователь с таким именем уже существует"
                        )
                    )
                )
                .switchIfEmpty(
                    Mono.defer(() -> repository.existsByEmail(user.getEmail()))
                )
                .filter(b -> Boolean.FALSE)
                .flatMap(exists ->
                    Mono.error(
                        new RuntimeException(
                            "Пользователь с таким email уже существует"
                        )
                    )
                )
                .switchIfEmpty(Mono.just(user))
                .then(Mono.just(user)) // продолжаем с пользователем
                .flatMap(repository::save)
        );
    }

    /**
     * Получение пользователя по имени
     */
    public Mono<User> getByUsername(String username) {
        return repository
            .findByUsername(username)
            .switchIfEmpty(
                Mono.error(
                    new UsernameNotFoundException("Пользователь не найден")
                )
            );
    }

    /**
     * Для интеграции с Spring Security
     */
    public ReactiveUserDetailsService userDetailsService() {
        return username ->
            getByUsername(username)
                .map(UserDetails.class::cast)
                .onErrorResume(UsernameNotFoundException.class, e ->
                    Mono.empty()
                );
    }

    /**
     * Получение текущего пользователя
     */
    public Mono<User> getCurrentUser() {
        return Mono.deferContextual(ctx -> {
            var auth = ctx.get(SecurityContext.class).getAuthentication();
            return getByUsername(auth.getName());
        });
    }

    /**
     * Выдача прав администратора
     */
    @Deprecated
    public Mono<Void> grantAdmin() {
        return getCurrentUser()
            .doOnNext(user -> user.setRole(Role.ROLE_ADMIN))
            .flatMap(repository::save)
            .then();
    }
}
