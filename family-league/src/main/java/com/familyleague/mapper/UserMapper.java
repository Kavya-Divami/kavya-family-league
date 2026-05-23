package com.familyleague.mapper;

import com.familyleague.dto.request.CreateUserRequest;
import com.familyleague.dto.response.UserResponse;
import com.familyleague.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    User toEntity(CreateUserRequest request);

    UserResponse toResponse(User user);
}
