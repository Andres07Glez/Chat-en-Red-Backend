package mx.edu.unpa.ChatEnRed.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mx.edu.unpa.ChatEnRed.DTOs.ConversationMember.Request.ConversationMemberRequest;
import mx.edu.unpa.ChatEnRed.DTOs.ConversationMember.Response.ConversationMemberResponse;
import mx.edu.unpa.ChatEnRed.domains.ConversationMember;
import mx.edu.unpa.ChatEnRed.domains.Conversation;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.domains.RoleStatus;

@Mapper(componentModel = "spring")
public interface ConversationMemberMapper {

    @Mapping(source = "conversation.id", target = "conversationId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "roleStatus.id", target = "roleStatusId")
    ConversationMemberResponse toResponse(ConversationMember entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "request.joinedAt", target = "joinedAt")
    @Mapping(source = "conversation", target = "conversation")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "roleStatus", target = "roleStatus")
    ConversationMember toEntity(ConversationMemberRequest request, Conversation conversation, User user, RoleStatus roleStatus);
}