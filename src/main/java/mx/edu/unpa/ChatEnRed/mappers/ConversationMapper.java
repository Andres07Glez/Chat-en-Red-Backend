package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request.ConversationRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Response.ConversationResponse;
import mx.edu.unpa.ChatEnRed.domains.Conversation;
import mx.edu.unpa.ChatEnRed.domains.ConversationType;
import mx.edu.unpa.ChatEnRed.domains.User;



@Mapper(componentModel="spring")

public interface ConversationMapper {
	@Mapping(source= "conversationType.id", target="conversationTypeId")
	@Mapping(source= "createdBy.id", target="createdById")
	ConversationResponse toResponse (Conversation entity);
	
	@Mapping(target= "id", ignore= true)
	@Mapping(source="request.createdAt", target="CreatedAt")
	@Mapping(source="request.updatedAt", target="updatedAt")
	@Mapping(source="conversationType", target ="conversationType")
	@Mapping(source="createdBy", target="createdBy")
	Conversation toEntity(ConversationRequest request, ConversationType convType, User createdBy);
}
