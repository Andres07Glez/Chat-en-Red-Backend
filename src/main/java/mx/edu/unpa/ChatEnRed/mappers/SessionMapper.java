package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.Session.Request.SessionRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Session.Response.SessionResponse;
import mx.edu.unpa.ChatEnRed.domains.Session;
import mx.edu.unpa.ChatEnRed.domains.User;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(source = "user.id", target = "userId")
    SessionResponse toResponse(Session entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "request.createdAt", target = "createdAt")
    @Mapping(source = "request.expiresAt", target = "expiresAt")
    @Mapping(source = "user", target = "user")
    Session toEntity(SessionRequest request, User user);
}