package com.familyleague.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.familyleague.dto.request.CreateUserRequest;
import com.familyleague.dto.request.UpdateUserRequest;
import com.familyleague.dto.response.ApiResponse;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.UserResponse;
import com.familyleague.enums.Role;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // needed by security filter
    @MockBean
    private com.familyleague.config.JwtService jwtService;

    @MockBean
    private com.familyleague.config.UserDetailsServiceImpl userDetailsService;

    private UserResponse sampleUser(UUID id) {
        return UserResponse.builder()
                .id(id)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_validRequest_returns201() throws Exception {
        UUID id = UUID.randomUUID();
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("testuser");
        req.setEmail("test@example.com");
        req.setPassword("password123");

        when(userService.createUser(any())).thenReturn(sampleUser(id));

        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_blankUsername_returns400() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("test@example.com");
        req.setPassword("password123");

        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserById_existingId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.getUserById(id)).thenReturn(sampleUser(id));

        mockMvc.perform(get("/api/v1/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserById_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.getUserById(id)).thenThrow(new ResourceNotFoundException("User", id));

        mockMvc.perform(get("/api/v1/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_returns200WithPage() throws Exception {
        UUID id = UUID.randomUUID();
        PagedResponse<UserResponse> paged = PagedResponse.<UserResponse>builder()
                .content(List.of(sampleUser(id)))
                .page(0).size(20).totalElements(1).totalPages(1).last(true)
                .build();
        when(userService.getAllUsers(0, 20, "createdAt", "desc", null, null)).thenReturn(paged);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_validRequest_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateUserRequest req = new UpdateUserRequest();
        req.setFirstName("Updated");

        when(userService.updateUser(eq(id), any())).thenReturn(sampleUser(id));

        mockMvc.perform(put("/api/v1/users/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_existingId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/v1/users/{id}", id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", UUID.randomUUID()).with(csrf()))
                .andExpect(status().isForbidden());
    }
}
