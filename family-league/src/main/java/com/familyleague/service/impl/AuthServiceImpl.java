package com.familyleague.service.impl;

import com.familyleague.config.JwtService;
import com.familyleague.dto.request.CreateUserRequest;
import com.familyleague.dto.request.LoginRequest;
import com.familyleague.dto.response.AuthResponse;
import com.familyleague.dto.response.UserResponse;
import com.familyleague.entity.User;
import com.familyleague.enums.Role;
import com.familyleague.repository.UserRepository;
import com.familyleague.service.AuthService;
import com.familyleague.service.UserService;

import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AuthResponse register(CreateUserRequest request) {
        log.debug("Registering new user: {}", request.getUsername());
        request.setRole(Role.USER);
        UserResponse userResponse = userService.createUser(request);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userResponse.getUsername());
        String token = jwtService.generateToken(userDetails);

        long expiresIn = jwtService.getExpirationMs() / 1000;
        log.info("User registered and token issued for username={}", userResponse.getUsername());
        return AuthResponse.builder()
                .token(token)
                .expiresIn(expiresIn)
                .expiresAt(OffsetDateTime.now().plusSeconds(expiresIn))
                .user(userResponse)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for: {}", request.getUsernameOrEmail());

        // Resolve username from either username or email
        String username = resolveUsername(request.getUsernameOrEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse userResponse = userService.getUserById(user.getId());

        long expiresIn = jwtService.getExpirationMs() / 1000;
        log.info("User logged in successfully: {}", username);
        return AuthResponse.builder()
                .token(token)
                .expiresIn(expiresIn)
                .expiresAt(OffsetDateTime.now().plusSeconds(expiresIn))
                .user(userResponse)
                .build();
    }

    private String resolveUsername(String usernameOrEmail) {
        if (usernameOrEmail.contains("@")) {
            return userRepository.findByEmailAndIsDeletedFalse(usernameOrEmail)
                    .map(User::getUsername)
                    .orElse(usernameOrEmail);
        }
        return usernameOrEmail;
    }
}
