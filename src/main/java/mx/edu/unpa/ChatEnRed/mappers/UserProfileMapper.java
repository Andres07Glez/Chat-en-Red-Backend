package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Request.UserProfileRequest;
import mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Response.UserProfileResponse;
import mx.edu.unpa.ChatEnRed.domains.UserProfile;
import mx.edu.unpa.ChatEnRed.domains.User;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(source = "user.id", target = "userId")
    UserProfileResponse toResponse(UserProfile profile);

    // construir nueva entidad a partir del DTO y la entidad User (getReferenceById)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "userId", ignore = true) // se rellena por @MapsId en la entidad
    @Mapping(target = "updatedAt", ignore = true) // lo gestiona @PrePersist/@PreUpdate
    UserProfile toEntity(UserProfileRequest dto, User user);
}