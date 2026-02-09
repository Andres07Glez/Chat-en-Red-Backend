package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.ChatListItemDTO;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.GroupMemberKeyDTO;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request.CreateGroupRequest;
import mx.edu.unpa.ChatEnRed.domains.*;
import mx.edu.unpa.ChatEnRed.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Request.ConversationRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Response.ConversationResponse;
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
    @Autowired
    private ConversationKeyRepository conversationKeyRepository;
    @PersistenceContext
    private EntityManager entityManager;

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
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + currentUsername));

        List<Conversation> conversations = conversationRepository.findConversationsByUserId(currentUser.getId());

        return conversations.stream().map(conv -> {
            ChatListItemDTO dto = new ChatListItemDTO();

            dto.setId(conv.getId());
            dto.setLastActivity(conv.getLastMessageAt());

            // Mapeo del último mensaje y su IV
            Message lastMsg = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conv.getId());
            if (lastMsg != null) {
                dto.setLastMessage(lastMsg.getContent());
                dto.setLastMessageIV(lastMsg.getIv()); // <--- Asegúrate de que el DTO tenga este campo también
            } else {
                dto.setLastMessage("");
            }

            // --- LÓGICA DE NOMBRE Y LLAVES ---
            String typeCode = conv.getConversationType().getCode(); // Asumiendo que getConversationType() no es null
            boolean isGroup = "GROUP".equals(typeCode);
            dto.setIsGroup(isGroup);

            if (isGroup) {
                // Caso Grupo: Nombre del grupo, SIN llave pública (por ahora)
                dto.setName(conv.getTitle());
                dto.setOtherUserPublicKey(null); // Explicitamente null en grupos
            } else {
                // Caso Directo: Nombre del otro usuario Y SU LLAVE PÚBLICA
                User otherUser = memberRepository.findOtherParticipant(conv.getId(), currentUser.getId());

                if (otherUser != null) {
                    dto.setName(otherUser.getUsername());
                    // === AQUÍ ESTÁ EL CAMBIO CLAVE ===
                    dto.setOtherUserPublicKey(otherUser.getPublicKey());
                    dto.setOtherUserId(otherUser.getId());
                } else {
                    dto.setName("Usuario Desconocido");
                    dto.setOtherUserPublicKey(null);
                }
            }

            // --- LÓGICA DE NO LEÍDOS (Tu código existente) ---
            ConversationMember member = memberRepository.findByConversationIdAndUserId(conv.getId(), currentUser.getId())
                    .orElse(null);
            long unread = 0;
            if (member != null) {
                if (member.getLastReadAt() != null) {
                    unread = messageRepository.countByConversationIdAndCreatedAtAfterAndSenderIdNot(
                            conv.getId(),
                            member.getLastReadAt(),
                            currentUser.getId());// <--- Excluir mis propios mensajes
                } else {
                    // Si nunca entré, cuento todo lo que no sea mío
                    unread = messageRepository.countByConversationIdAndSenderIdNot(
                            conv.getId(),
                            currentUser.getId());
                }
            }
            dto.setUnreadCount((int) unread);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Integer conversationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        ConversationMember member = memberRepository.findByConversationIdAndUserId(conversationId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("No eres miembro de este chat"));

        // Actualizamos la fecha a "AHORA MISMO"
        member.setLastReadAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void createGroup(CreateGroupRequest request, String creatorUsername) {

        // 1. Obtener al creador (Query real a BD)
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new EntityNotFoundException("Usuario creador no encontrado"));

        // 2. Obtener referencias (Proxies) para los catálogos fijos
        // No hace SELECT a la BD, solo crea un objeto con el ID puesto.
        ConversationType groupType = entityManager.getReference(ConversationType.class, 2); // 2 = GROUP
        RoleStatus ownerRole = entityManager.getReference(RoleStatus.class, 3); // 3 = OWNER
        RoleStatus memberRole = entityManager.getReference(RoleStatus.class, 1); // 1 = MEMBER

        // 3. Crear la Conversación usando BUILDER
        Conversation conversation = Conversation.builder()
                .title(request.getTitle())
                .createdBy(creator)
                .conversationType(groupType) // Asignamos el proxy
                // createdAt y lastMessageAt se llenan solos con @PrePersist o los pones aquí
                .build();

        conversation = conversationRepository.save(conversation);

        // 4. Procesar Miembros
        for (GroupMemberKeyDTO memberDto : request.getMembers()) {

            // Si el miembro es el creador, ya tenemos el objeto 'creator', si no, buscamos
            User memberUser;
            if (memberDto.getUserId().equals(creator.getId())) {
                memberUser = creator;
            } else {
                memberUser = userRepository.findById(memberDto.getUserId())
                        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
            }

            // A. Crear Miembro con BUILDER
            ConversationMember membership = ConversationMember.builder()
                    .conversation(conversation)
                    .user(memberUser)
                    .roleStatus(memberUser.getId().equals(creator.getId()) ? ownerRole : memberRole)
                    .joinedAt(LocalDateTime.now())
                    .build();

            memberRepository.save(membership);

            // B. Guardar Llave (Sender Key) con BUILDER
            ConversationKey conversationKey = ConversationKey.builder()
                    .conversation(conversation)
                    .user(memberUser)
                    .encryptedKey(memberDto.getEncryptedKey())
                    .iv(memberDto.getIv())
                    .build();

            conversationKeyRepository.save(conversationKey);
        }
    }
}