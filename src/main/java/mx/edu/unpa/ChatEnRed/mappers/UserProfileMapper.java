package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Request.UserProfileRequest;
import mx.edu.unpa.ChatEnRed.DTOs.UserProfile.Response.UserProfileResponse;
import mx.edu.unpa.ChatEnRed.domains.UserProfile;
import mx.edu.unpa.ChatEnRed.domains.User;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(source = "user.userId", target = "userId", ignore = true) // UserProfile stores userId as PK via @MapsId; mapping handled by JPA
    @Mapping(source = "user.userId", target = "userId", qualifiedByName = "userIdFromUser", ignore = true)
    @Mapping(source = "user.id", target = "userId")
    UserProfileResponse toResponse(UserProfile entity);

    @Mapping(target = "userId", ignore = true) // userId shared PK handled via @MapsId when setting user
    @Mapping(source = "request.updatedAt", target = "updatedAt")
    @Mapping(source = "user", target = "user")
    UserProfile toEntity(UserProfileRequest request, User user);
}