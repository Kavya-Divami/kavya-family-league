package com.familyleague.service.impl;

import com.familyleague.dto.request.CreateUserRequest;
import com.familyleague.dto.request.UpdateUserRequest;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.UserResponse;
import com.familyleague.entity.User;
import com.familyleague.enums.Role;
import com.familyleague.exception.DuplicateResourceException;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.mapper.UserMapper;
import com.familyleague.repository.UserRepository;
import com.familyleague.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.debug("Creating user with username: {}", request.getUsername());

        if (userRepository.existsByUsernameAndIsDeletedFalse(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);
        log.info("Created user id={} username={}", saved.getId(), saved.getUsername());
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userMapper.toResponse(findActiveUser(id));
    }

    @Override
    public PagedResponse<UserResponse> getAllUsers(int page, int size, String sortBy,
                                                   String sortDir, String search, Role role) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = (search != null || role != null)
                ? userRepository.searchUsers(search, role, pageable)
                : userRepository.findAllByIsDeletedFalse(pageable);

        return PagedResponse.<UserResponse>builder()
                .content(userPage.getContent().stream().map(userMapper::toResponse).toList())
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        log.debug("Updating user id={}", id);
        User user = findActiveUser(id);

        if (request.getFirstName() != null)   user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)    user.setLastName(request.getLastName());
        if (request.getAvatarName() != null)  user.setAvatarName(request.getAvatarName());
        if (request.getProfilePicUrl() != null) user.setProfilePicUrl(request.getProfilePicUrl());

        User saved = userRepository.save(user);
        log.info("Updated user id={}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {
        log.debug("Deactivating user id={}", id);
        User user = findActiveUser(id);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("Deactivated user id={}", id);
    }

    @Override
    @Transactional
    public void activateUser(UUID id) {
        log.debug("Activating user id={}", id);
        User user = findActiveUser(id);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("Activated user id={}", id);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.debug("Soft-deleting user id={}", id);
        User user = findActiveUser(id);
        user.setIsDeleted(true);
        user.setDeletedAt(OffsetDateTime.now());
        user.setDeletedBy(currentPrincipal());
        userRepository.save(user);
        log.info("Soft-deleted user id={}", id);
    }

    private User findActiveUser(UUID id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private String currentPrincipal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }
}
