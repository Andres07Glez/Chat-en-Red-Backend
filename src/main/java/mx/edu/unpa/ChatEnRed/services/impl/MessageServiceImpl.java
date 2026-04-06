package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mx.edu.unpa.ChatEnRed.domains.*;
import mx.edu.unpa.ChatEnRed.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Request.MessageRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.MessageResponse;
import mx.edu.unpa.ChatEnRed.mappers.MessageMapper;
import mx.edu.unpa.ChatEnRed.services.MessageService;

@Service
public class MessageServiceImpl implements MessageService{
	
	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private ConversationRepository conversationRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MessageMapper messageMapper;
	@Autowired
	private MessageTypeRepository messageTypeRepository;
	@Autowired
	private ConversationMemberRepository memberRepository;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	

	@Override
	@Transactional(readOnly=true)
	public List<MessageResponse> findAll() {
		return this.messageRepository.findAll().stream()
				.map(messageMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<MessageResponse> findById(Integer id) {
		return this.messageRepository.findById(id)
				.map(messageMapper::toResponse);
	}

	@Override
	@Transactional
	public Optional<Boolean> deleteById(Integer id) {
		return this.messageRepository.findById(id)
				.map(message->{
					message.setDeletedAt(LocalDateTime.now());
					this.messageRepository.save(message);
				return true;
				});
		
	}

	@Override
	@Transactional
	public List<MessageResponse> getChatMessages(Integer conversationId, String currentUsername) {
		User currentUser = userRepository.findByUsername(currentUsername)
				.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

		// 2. SEGURIDAD: Verificar que el usuario pertenezca al chat
		boolean isMember = memberRepository.existsByConversationIdAndUserId(conversationId, currentUser.getId());

		if (!isMember) {
			throw new AccessDeniedException("No tienes permiso para ver los mensajes de este chat.");
		}

		// 3. Obtener mensajes de la BD (Cifrados)
		List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
		ConversationMember member = memberRepository.findByConversationIdAndUserId(conversationId, currentUser.getId())
				.orElseThrow(() -> new AccessDeniedException("No eres miembro"));

		member.setLastReadAt(LocalDateTime.now());
		memberRepository.save(member);

		// 4. Convertir a DTO y calcular isMine
		return messages.stream().map(msg -> {
			MessageResponse dto = messageMapper.toResponse(msg);
			if (msg.getSender() != null) {
				dto.setMine(msg.getSender().getId().equals(currentUser.getId()));
			} else {
				// Mensajes de sistema (ej: "Juan salió del grupo") no son míos
				dto.setSenderName("Sistema");
				dto.setMine(false);
			}
			// IMPORTANTE: Aquí NO desciframos. Mandamos el content (cifrado) y el iv.
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public MessageResponse sendMessage(MessageRequest request, String username) {
		User sender = userRepository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

		// 2. Obtener conversación
		Conversation conversation = conversationRepository.findById(request.getConversationId()) // Asumiendo que inyectaste ConversationRepository
				.orElseThrow(() -> new EntityNotFoundException("Conversación no encontrada"));

		// 3. SEGURIDAD: Verificar membresía
		boolean isMember = memberRepository.existsByConversationIdAndUserId(conversation.getId(), sender.getId());
		if (!isMember) {
			throw new AccessDeniedException("No puedes enviar mensajes a este chat.");
		}

		// 4. Obtener Tipo de Mensaje (TEXT por defecto)
		String typeCode = request.getMessageTypeCode() != null ? request.getMessageTypeCode() : "TEXT";
		MessageType msgType = messageTypeRepository.findByCode(typeCode)
				.orElseThrow(() -> new EntityNotFoundException("Tipo de mensaje inválido"));

		// 5. Mapear DTO -> Entidad (Usando tu Mapper existente)
		Message newMessage = messageMapper.toEntity(request, conversation, sender, msgType);
		newMessage.setContent(request.getContent());
		newMessage.setIv(request.getIv());

		// 6. Guardar
		Message savedMessage = messageRepository.save(newMessage);

		// 7. Actualizar "Last Message" en la conversación (Para que suba en la lista de chats)
		conversation.setLastMessageAt(savedMessage.getCreatedAt());
		conversationRepository.save(conversation);

		// 8. Convertir a Respuesta
		MessageResponse response = messageMapper.toResponse(savedMessage);
		// Configurar nombre del remitente
		response.setSenderName(sender.getUsername());

		// Preparamos el mensaje para el WebSocket
		// Importante: Para el que recibe el mensaje por WS, 'mine' es false por defecto.
		response.setMine(false);

		// 1. Broadcast  A LA SALA
		String chatDestination = "/topic/chat/" + request.getConversationId();
		messagingTemplate.convertAndSend(chatDestination, response);

		// Notificación personal a cada miembro (para actualizar preview en chat-list)
		memberRepository.findUserIdsByConversationId(request.getConversationId())
				.forEach(userId ->
						messagingTemplate.convertAndSend("/topic/user/" + userId, response));

		//  Ajuste final para la respuesta HTTP (Para ti mismo, que acabas de enviar)
		response.setMine(true);

		return response;
	}

	@Override
	@Transactional
	public int deleteMessages(List<Integer> messageIds, String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

		// Solo carga los mensajes que realmente son del usuario (filtro en BD)
		List<Message> ownMessages = messageRepository.findByIdInAndSenderId(messageIds, user.getId());

		if (ownMessages.isEmpty()) return 0;

		LocalDateTime now = LocalDateTime.now();
		ownMessages.forEach(msg -> msg.setDeletedAt(now));
		messageRepository.saveAll(ownMessages);

		return ownMessages.size();
	}


}
