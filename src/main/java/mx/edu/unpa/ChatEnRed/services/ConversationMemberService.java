package mx.edu.unpa.ChatEnRed.services;

import java.util.List;
import java.util.Optional;

import mx.edu.unpa.ChatEnRed.DTOs.ConversationMember.Request.ConversationMemberRequest;
import mx.edu.unpa.ChatEnRed.DTOs.ConversationMember.Response.ConversationMemberResponse;

public interface ConversationMemberService {
    List<ConversationMemberResponse> findAll();
    Optional<ConversationMemberResponse> findById(Integer id);
    Optional<ConversationMemberResponse> save(ConversationMemberRequest request);
    Optional<Boolean> deleteById(Integer id);
    Optional<ConversationMemberResponse> update(Integer id, ConversationMemberRequest request);
}