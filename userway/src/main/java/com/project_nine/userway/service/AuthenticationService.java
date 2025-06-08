// package com.project_nine.userway.service;

// import com.project_nine.userway.domain.dto.JwtAuthenticationResponse;
// import com.project_nine.userway.domain.dto.SignInRequest;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.ReactiveAuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.stereotype.Service;

// import reactor.core.publisher.Mono;
// @Service
// public class AuthenticationService {
//     @Autowired
//     private ReactiveAuthenticationManager authenticationManager;

//     public Mono<JwtAuthenticationResponse> signIn(SignInRequest request) {
//         var authenticationToken = new UsernamePasswordAuthenticationToken(
//             request.getUsername(),
//             request.getPassword()
//         );

//         return authenticationManager.authenticate(authenticationToken)
//             .flatMap(authentication -> {
//                 // Реактивно загружаем пользователя
//                 return userService
//                     .userDetailsService()
//                     .loadUserByUsername(request.getUsername());
//             })
//             .map(userDetails -> {
//                 // Генерируем токен
//                 String jwt = jwtService.generateToken(userDetails);
//                 return new JwtAuthenticationResponse(jwt);
//             });
//     }
// }
