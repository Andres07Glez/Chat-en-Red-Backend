package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.AuditEvent.Request.AuditEventRequest;
import mx.edu.unpa.ChatEnRed.DTOs.AuditEvent.Response.AuditEventResponse;
import mx.edu.unpa.ChatEnRed.domains.AuditEvent;
import mx.edu.unpa.ChatEnRed.domains.User;

@Mapper(componentModel = "spring")
public interface AuditEventMapper {

    @Mapping(source = "user.id", target = "userId")
    AuditEventResponse toResponse(AuditEvent entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "request.createdAt", target = "createdAt")
    @Mapping(source = "user", target = "user")
    AuditEvent toEntity(AuditEventRequest request, User user);
}