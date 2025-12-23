package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.Device.Request.DeviceRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Device.Response.DeviceResponse;
import mx.edu.unpa.ChatEnRed.domains.Device;
import mx.edu.unpa.ChatEnRed.domains.User;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(source = "user.id", target = "userId")
    DeviceResponse toResponse(Device entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "request.createdAt", target = "createdAt")
    @Mapping(source = "user", target = "user")
    Device toEntity(DeviceRequest request, User user);
}