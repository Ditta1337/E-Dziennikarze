package com.edziennikarze.gradebook.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.edziennikarze.gradebook.auth.dto.AuthRequest;
import com.edziennikarze.gradebook.auth.dto.AuthResponse;
import com.edziennikarze.gradebook.auth.dto.RefreshTokenRequest;
import com.edziennikarze.gradebook.auth.jwt.JwtUtil;
import com.edziennikarze.gradebook.user.UserService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ReactiveAuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final UserService userService;

    public Mono<AuthResponse> login(AuthRequest authRequest) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()))
                .flatMap(authentication -> {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    String accessToken = jwtUtil.generateToken(userDetails);
                    String refreshToken = jwtUtil.generateRefreshToken(userDetails);

                    return Mono.just(buildAuthResponse(accessToken, refreshToken));
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")));
    }

    public Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String username = jwtUtil.extractUsername(refreshToken);

        return userService.findByUsername(username)
                .flatMap(userDetails -> {
                    if ( Boolean.TRUE.equals(jwtUtil.validateToken(refreshToken, userDetails)) ) {
                        String newAccessToken = jwtUtil.generateToken(userDetails);
                        return Mono.just(buildAuthResponse(newAccessToken, refreshToken));
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
                    }
                });
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}