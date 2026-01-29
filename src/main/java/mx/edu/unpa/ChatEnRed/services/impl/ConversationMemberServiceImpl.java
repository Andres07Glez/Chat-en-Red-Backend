package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import mx.edu.unpa.ChatEnRed.DTOs.ConversationMember.Request.ConversationMemberRequest;
import mx.edu.unpa.ChatEnRed.DTOs.ConversationMember.Response.ConversationMemberResponse;
import mx.edu.unpa.ChatEnRed.domains.Conversation;
import mx.edu.unpa.ChatEnRed.domains.ConversationMember;
import mx.edu.unpa.ChatEnRed.domains.RoleStatus;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.mappers.ConversationMemberMapper;
import mx.edu.unpa.ChatEnRed.repositories.ConversationMemberRepository;
import mx.edu.unpa.ChatEnRed.repositories.ConversationRepository;
import mx.edu.unpa.ChatEnRed.repositories.RoleStatusRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import mx.edu.unpa.ChatEnRed.services.ConversationMemberService;

@Service
public class ConversationMemberServiceImpl implements ConversationMemberService {

    @Autowired
    private ConversationMemberRepository conversationMemberRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleStatusRepository roleStatusRepository;

    @Autowired
    private ConversationMemberMapper conversationMemberMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationMemberResponse> findAll() {
        return conversationMemberRepository.findAll().stream()
                .map(conversationMemberMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConversationMemberResponse> findById(Integer id) {
        return conversationMemberRepository.findById(id)
                .map(conversationMemberMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<ConversationMemberResponse> save(ConversationMemberRequest request) {
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found with id: " + request.getConversationId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        RoleStatus roleStatus = roleStatusRepository.findById(request.getRoleStatusId())
                .orElseThrow(() -> new EntityNotFoundException("RoleStatus not found with id: " + request.getRoleStatusId()));

        ConversationMember entity = conversationMemberMapper.toEntity(request, conversation, user, roleStatus);

        ConversationMember saved = conversationMemberRepository.save(entity);
        return Optional.of(conversationMemberMapper.toResponse(saved));
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        return conversationMemberRepository.findById(id)
                .map(entity -> {
                    conversationMemberRepository.deleteById(id);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<ConversationMemberResponse> update(Integer id, ConversationMemberRequest request) {
        ConversationMember existing = conversationMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ConversationMember not found: " + id));

        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found with id: " + request.getConversationId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        RoleStatus roleStatus = roleStatusRepository.findById(request.getRoleStatusId())
                .orElseThrow(() -> new EntityNotFoundException("RoleStatus not found with id: " + request.getRoleStatusId()));

        existing.setConversation(conversation);
        existing.setUser(user);
        existing.setRoleStatus(roleStatus);
        existing.setJoinedAt(request.getJoinedAt() != null ? request.getJoinedAt() : LocalDateTime.now());

        return Optional.of(existing)
        		.map(conversationMemberRepository::save)
        		.map(conversationMemberMapper::toResponse);

    }
}