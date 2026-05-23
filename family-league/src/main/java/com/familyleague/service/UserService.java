package com.familyleague.service;

import com.familyleague.dto.request.CreateUserRequest;
import com.familyleague.dto.request.UpdateUserRequest;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.UserResponse;
import com.familyleague.enums.Role;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(UUID id);

    PagedResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir,
                                           String search, Role role);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    void deactivateUser(UUID id);

    void activateUser(UUID id);

    void deleteUser(UUID id);
}
