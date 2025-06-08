package com.project_nine.userway.config;

import com.project_nine.userway.service.JwtService;
import io.jsonwebtoken.Claims;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class AuthManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String userName;

        try {
            userName = jwtService.extractUserName(authToken);
        } catch (Exception e) {
            userName = null;
            e.printStackTrace();
        }

        if (userName != null && jwtService.isTokenValid(authToken)) {
            Claims claims = jwtService.extractAllClaims(authToken);
            System.out.println(claims.toString());
            String role = claims.get("role", String.class);
            List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(role)
            );
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    userName,
                    null,
                    authorities
                );

            return Mono.just(authenticationToken);
        } else {
            return Mono.empty();
        }
    }
}
