package mx.edu.unpa.ChatEnRed.mappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.Attachment.Request.AttachmentRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Attachment.Response.AttachmentResponse;
import mx.edu.unpa.ChatEnRed.domains.Attachment;
import mx.edu.unpa.ChatEnRed.domains.Message;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    @Mapping(source = "message.id", target = "messageId")
    AttachmentResponse toResponse(Attachment entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "request.createdAt", target = "createdAt")
    @Mapping(source = "message", target = "message")
    Attachment toEntity(AttachmentRequest request, Message message);
}
