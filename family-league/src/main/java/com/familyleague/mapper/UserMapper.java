package com.familyleague.mapper;

import com.familyleague.dto.request.CreateUserRequest;
import com.familyleague.dto.response.UserResponse;
import com.familyleague.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "isDeleted", constant = "false")
    User toEntity(CreateUserRequest request);

    UserResponse toResponse(User user);
}
