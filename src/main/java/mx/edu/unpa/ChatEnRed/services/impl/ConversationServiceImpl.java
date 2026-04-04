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
import mx.edu.unpa.ChatEnRed.DTOs.Conversation.Response.ConversationKeyResponse;
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
        ConversationType ct = conversationTypeRepository.findById(request.getConversationTypeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "ConversationType not found: " + request.getConversationTypeId()));

        User createdBy = resolveUser(request.getCreatedById());
        Conversation conv = conversationMapper.toEntity(request, ct, createdBy);

        return Optional.of(conversationRepository.save(conv)).map(conversationMapper::toResponse);
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
                .orElseThrow(() -> new EntityNotFoundException(
                        "ConversationType not found: " + request.getConversationTypeId()));

        existing.setConversationType(ct);
        existing.setTitle(request.getTitle());
        existing.setCreatedBy(resolveUser(request.getCreatedById()));

        return Optional.of(conversationRepository.save(existing)).map(conversationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatListItemDTO> getMyChatList(String currentUsername) {
        User currentUser = findUserByUsername(currentUsername);
        List<Conversation> conversations = conversationRepository.findConversationsByUserId(currentUser.getId());

        return conversations.stream()
                .map(conv -> buildChatListItemDTO(conv, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Integer conversationId, String username) {
        User user = findUserByUsername(username);
        ConversationMember member = memberRepository
                .findByConversationIdAndUserId(conversationId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("No eres miembro de este chat"));

        member.setLastReadAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public ChatListItemDTO createGroup(CreateGroupRequest request, String creatorUsername) {

        User creator = findUserByUsername(creatorUsername);

        // IDs fijos de catálogos (se sincronizan con Liquibase)
        ConversationType groupType = entityManager.getReference(ConversationType.class, 2); // GROUP
        RoleStatus ownerRole       = entityManager.getReference(RoleStatus.class, 3);       // OWNER
        RoleStatus memberRole      = entityManager.getReference(RoleStatus.class, 1);       // MEMBER

        // 1. Crear conversación
        Conversation conversation = conversationRepository.save(
                Conversation.builder()
                        .title(request.getTitle())
                        .createdBy(creator)
                        .conversationType(groupType)
                        .build()
        );

        // 2. Crear miembro + llave cifrada por cada participante
        for (GroupMemberKeyDTO memberDto : request.getMembers()) {
            User memberUser = memberDto.getUserId().equals(creator.getId())
                    ? creator
                    : userRepository.findById(memberDto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Usuario no encontrado: " + memberDto.getUserId()));

            boolean isCreator = memberUser.getId().equals(creator.getId());

            memberRepository.save(ConversationMember.builder()
                    .conversation(conversation)
                    .user(memberUser)
                    .roleStatus(isCreator ? ownerRole : memberRole)
                    .joinedAt(LocalDateTime.now())
                    .build());

            conversationKeyRepository.save(ConversationKey.builder()
                    .conversation(conversation)
                    .user(memberUser)
                    .encryptedKey(memberDto.getEncryptedKey())
                    .iv(memberDto.getIv())
                    .build());
        }

        // 3. Devolver DTO listo para abrir el ChatWindow
        ChatListItemDTO dto = new ChatListItemDTO();
        dto.setId(conversation.getId());
        dto.setName(request.getTitle());
        dto.setIsGroup(true);
        dto.setOtherUserId(null);
        dto.setOtherUserPublicKey(null);
        dto.setUnreadCount(0);
        dto.setLastActivity(conversation.getLastMessageAt());
        dto.setLastMessage("");

        return dto;
    }
    // ── Chat directo (find or create) ─────────────────────────────────────────

    @Override
    @Transactional
    public ChatListItemDTO findOrCreateDirectConversation(String currentUsername, Integer targetUserId) {
        User currentUser = findUserByUsername(currentUsername);
        User targetUser  = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario destino no encontrado: " + targetUserId));

        Conversation conversation = conversationRepository
                .findDirectConversationBetweenUsers(currentUser.getId(), targetUser.getId())
                .orElseGet(() -> createDirectConversation(currentUser, targetUser));

        return buildDirectChatDTO(conversation, targetUser);
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationKeyResponse getMyConversationKey(Integer conversationId, String username) {
        User user = findUserByUsername(username);

        ConversationKey myKey = conversationKeyRepository
                .findWithCreatorByConversationIdAndUserId(conversationId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró la llave del grupo para conversationId= " + conversationId + ", usuario=" + username));

        // La llave fue cifrada con ECDH(creador_privada, miembro_pública).
        // Para descifrarla, el cliente necesita la pública del creador.
        User creator = myKey.getConversation().getCreatedBy();
        if (creator == null) {
            throw new EntityNotFoundException("La conversación " + conversationId + " no tiene creador definido");
        }

        return new ConversationKeyResponse(
                myKey.getEncryptedKey(),
                myKey.getIv(),
                creator.getPublicKey()
        );
    }
    // ── Helpers privados ──────────────────────────────────────────────────────

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + username));
    }

    private User resolveUser(Integer userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    }

    private Conversation createDirectConversation(User userA, User userB) {
        ConversationType directType = entityManager.getReference(ConversationType.class, 1);
        RoleStatus memberRole       = entityManager.getReference(RoleStatus.class, 1);

        Conversation conv = conversationRepository.save(Conversation.builder()
                .conversationType(directType)
                .createdBy(userA)
                .build());

        memberRepository.save(ConversationMember.builder()
                .conversation(conv).user(userA).roleStatus(memberRole)
                .joinedAt(LocalDateTime.now()).build());

        memberRepository.save(ConversationMember.builder()
                .conversation(conv).user(userB).roleStatus(memberRole)
                .joinedAt(LocalDateTime.now()).build());

        return conv;
    }

    private ChatListItemDTO buildDirectChatDTO(Conversation conversation, User otherUser) {
        ChatListItemDTO dto = new ChatListItemDTO();
        dto.setId(conversation.getId());
        dto.setName(otherUser.getUsername());
        dto.setIsGroup(false);
        dto.setOtherUserId(otherUser.getId());
        dto.setOtherUserPublicKey(otherUser.getPublicKey());
        dto.setUnreadCount(0);
        dto.setLastActivity(conversation.getLastMessageAt());

        Message lastMsg = messageRepository
                .findFirstByConversationIdOrderByCreatedAtDesc(conversation.getId());
        dto.setLastMessage(lastMsg != null ? lastMsg.getContent() : "");
        dto.setLastMessageIV(lastMsg != null ? lastMsg.getIv() : null);

        return dto;
    }

    private ChatListItemDTO buildChatListItemDTO(Conversation conv, User currentUser) {
        ChatListItemDTO dto = new ChatListItemDTO();
        dto.setId(conv.getId());
        dto.setLastActivity(conv.getLastMessageAt());

        Message lastMsg = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conv.getId());
        dto.setLastMessage(lastMsg != null ? lastMsg.getContent() : "");
        dto.setLastMessageIV(lastMsg != null ? lastMsg.getIv() : null);

        boolean isGroup = "GROUP".equals(conv.getConversationType().getCode());
        dto.setIsGroup(isGroup);

        if (isGroup) {
            dto.setName(conv.getTitle());
            dto.setOtherUserPublicKey(null);
        } else {
            User other = memberRepository.findOtherParticipant(conv.getId(), currentUser.getId());
            if (other != null) {
                dto.setName(other.getUsername());
                dto.setOtherUserPublicKey(other.getPublicKey());
                dto.setOtherUserId(other.getId());
            } else {
                dto.setName("Usuario Desconocido");
            }
        }

        ConversationMember member = memberRepository
                .findByConversationIdAndUserId(conv.getId(), currentUser.getId()).orElse(null);

        long unread = 0;
        if (member != null) {
            unread = (member.getLastReadAt() != null)
                    ? messageRepository.countByConversationIdAndCreatedAtAfterAndSenderIdNot(
                    conv.getId(), member.getLastReadAt(), currentUser.getId())
                    : messageRepository.countByConversationIdAndSenderIdNot(
                    conv.getId(), currentUser.getId());
        }
        dto.setUnreadCount((int) unread);

        return dto;
    }
}