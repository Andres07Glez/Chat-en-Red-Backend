package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.ChatListItemDTO;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request.ConversationRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request.CreateGroupRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Response.ConversationResponse;
import mx.edu.unpa.ChatEnRed.domains.Conversation;

public interface ConversationService {
	public List<ConversationResponse> findAll();
    public Optional<ConversationResponse> findById(Integer id);
    public Optional<ConversationResponse> save(ConversationRequest request);
    public Optional<Boolean> deleteById(Integer id);
    public Optional<ConversationResponse> update(Integer id, ConversationRequest request);
    List<ChatListItemDTO> getMyChatList(String currentUsername);
    void markAsRead(Integer conversationId, String username);

    void createGroup(CreateGroupRequest request, String username);
}