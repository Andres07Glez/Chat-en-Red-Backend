package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.ChatListItemDTO;
import mx.edu.unpa.ChatEnRed.domains.Message;
import mx.edu.unpa.ChatEnRed.repositories.*;
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

    @Autowired
    private ConversationMemberRepository memberRepository;
    @Autowired
    private MessageRepository messageRepository;

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
    public List<ChatListItemDTO> getMyChatList(String currentUsername) {

        // 1. Obtener al usuario autenticado
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + currentUsername));

        // 2. Traer las conversaciones desde la BD
        List<Conversation> conversations = conversationRepository.findConversationsByUserId(currentUser.getId());

        // 3. Transformar Entidad -> DTO con lógica de negocio
        return conversations.stream().map(conv -> {
            ChatListItemDTO dto = new ChatListItemDTO();
            // Datos básicos
            dto.setId(conv.getId());
            dto.setLastActivity(conv.getLastMessageAt());
            dto.setUnreadCount(0); // Pendiente para el futuro
            // dto.setLastMessage("..."); // Pendiente: Requiere consulta a tabla Messages
            Message lastMsg = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conv.getId());
            if (lastMsg != null) {
                // NOTA: Si implementamos cifrado después, aquí vendrá texto cifrado.
                // El frontend se encargará de descifrarlo o mostraremos "Mensaje cifrado".
                // Por ahora (texto plano) lo mandamos directo.
                dto.setLastMessage(lastMsg.getContent());
            } else {
                dto.setLastMessage(""); // Chat vacío
            }

            // --- LÓGICA CRÍTICA: Determinar Nombre e Icono ---
            // Usamos el CODE, que es seguro y legible ("GROUP", "DIRECT")
            String typeCode = conv.getConversationType().getCode();
            boolean isGroup = "GROUP".equals(typeCode);
            dto.setIsGroup(isGroup);

            if (isGroup) {
                // Caso A: Es un Grupo -> El nombre es el título del grupo
                dto.setName(conv.getTitle());
            } else {
                // Caso B: Es Directo -> El nombre es el de la OTRA persona
                User otherUser = memberRepository.findOtherParticipant(conv.getId(), currentUser.getId());
                dto.setName(otherUser != null ? otherUser.getUsername() : "Usuario Desconocido");
            }

            return dto;
        }).collect(Collectors.toList());
    }
}