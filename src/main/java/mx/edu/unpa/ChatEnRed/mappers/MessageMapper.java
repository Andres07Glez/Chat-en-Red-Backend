package mx.edu.unpa.ChatEnRed.mappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.Message.Request.MessageRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.MessageResponse;
import mx.edu.unpa.ChatEnRed.domains.Conversation;
import mx.edu.unpa.ChatEnRed.domains.Message;
import mx.edu.unpa.ChatEnRed.domains.MessageType;
import mx.edu.unpa.ChatEnRed.domains.User;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(source = "conversation.id", target = "conversationId")
    @Mapping(source = "sender.id", target = "senderId")
    //@Mapping(source = "sender.username", target = "senderUsername")
    @Mapping(source = "messageType.code", target = "messageTypeCode")
    MessageResponse toResponse(Message message);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(source="conversation", target= "conversation")
    @Mapping(source="sender", target= "sender")
    @Mapping(source="messageType", target= "messageType")
    @Mapping(source = "request.createdAt", target = "createdAt")
    @Mapping(source = "request.editedAt", target = "editedAt")
    @Mapping(source = "request.iv",target = "iv")
    Message toEntity(MessageRequest request,Conversation conversation,User sender,MessageType messageType);
}
