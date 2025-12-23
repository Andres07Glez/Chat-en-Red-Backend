package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.MessageStatus.Request.MessageStatusRequest;
import mx.edu.unpa.ChatEnRed.DTOs.MessageStatus.Response.MessageStatusResponse;
import mx.edu.unpa.ChatEnRed.domains.MessageStatus;
import mx.edu.unpa.ChatEnRed.domains.Message;
import mx.edu.unpa.ChatEnRed.domains.User;

@Mapper(componentModel = "spring")
public interface MessageStatusMapper {

    @Mapping(source = "message.id", target = "messageId")
    @Mapping(source = "recipient.id", target = "recipientId")
    MessageStatusResponse toResponse(MessageStatus entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "request.delivered", target = "delivered")
    @Mapping(source = "request.deliveredAt", target = "deliveredAt")
    @Mapping(source = "request.reading", target = "reading")
    @Mapping(source = "request.readAt", target = "readAt")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "recipient", target = "recipient")
    MessageStatus toEntity(MessageStatusRequest request, Message message, User recipient);
}