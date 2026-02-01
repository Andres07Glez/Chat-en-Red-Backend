package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.User.Request.UserRequest;
import mx.edu.unpa.ChatEnRed.DTOs.User.Response.UserResponse;
import mx.edu.unpa.ChatEnRed.domains.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // Lo ignoramos aquí para encriptarlo en el Service
    User toEntity(UserRequest request);
}