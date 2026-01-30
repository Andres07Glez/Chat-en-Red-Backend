package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.ChatListItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request.ConversationRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Response.ConversationResponse;
import mx.edu.unpa.ChatEnRed.domains.Conversation;
import mx.edu.unpa.ChatEnRed.domains.ConversationType;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.mappers.ConversationMapper;
import mx.edu.unpa.ChatEnRed.repositories.ConversationRepository;
import mx.edu.unpa.ChatEnRed.repositories.ConversationTypeRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import mx.edu.unpa.ChatEnRed.services.ConversationService;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationTypeRepository conversationTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationMapper conversationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> findAll() {
        return conversationRepository.findAll().stream()
                .map(conversationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConversationResponse> findById(Integer id) {
        return conversationRepository.findById(id)
                .map(conversationMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<ConversationResponse> save(ConversationRequest request) {
    	
        ConversationType ct = this.conversationTypeRepository.findById(request.getConversationTypeId())
                .orElseThrow(() -> new EntityNotFoundException("ConversationType not found with id: " + request.getConversationTypeId()));

        User createdBy = null;
        if (request.getCreatedById() != null) {
            createdBy = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getCreatedById()));
        }

        Conversation conversation = conversationMapper.toEntity(request, ct, createdBy);
        
        return Optional.of(conversation)
				.map(conversationRepository::save)
				.map(conversationMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        return conversationRepository.findById(id)
                .map(entity -> {
                    conversationRepository.deleteById(id);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<ConversationResponse> update(Integer id, ConversationRequest request) {
        Conversation existing = conversationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found: " + id));

        ConversationType ct = conversationTypeRepository.findById(request.getConversationTypeId())
                .orElseThrow(() -> new EntityNotFoundException("ConversationType not found with id: " + request.getConversationTypeId()));

        User createdBy = null;
        if (request.getCreatedById() != null) {
            createdBy = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getCreatedById()));
        }

        existing.setConversationType(ct);
        existing.setTitle(request.getTitle());
        existing.setCreatedBy(createdBy);
        // if (request.getCreatedAt() != null) existing.setCreatedAt(request.getCreatedAt());

        return Optional.of(existing)
        		.map(conversationRepository::save)
        		.map(conversationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatListItemDTO> getChatList(Integer userId) {
        //Validar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }

        // Ejecutar la consulta nativa optimizada del Repositorio
        // Esta consulta ya trae el último mensaje, el contador de no leídos
        // y ordena por last_message_at DESC.
        return conversationRepository.findChatListByUserId(userId);
    }
}