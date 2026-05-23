package com.familyleague.service;

import com.familyleague.dto.request.CreateUserRequest;
import com.familyleague.dto.request.LoginRequest;
import com.familyleague.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(CreateUserRequest request);

    AuthResponse login(LoginRequest request);
}
