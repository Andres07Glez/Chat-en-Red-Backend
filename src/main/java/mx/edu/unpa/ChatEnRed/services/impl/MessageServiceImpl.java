package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mx.edu.unpa.ChatEnRed.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Request.MessageRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Message.Response.MessageResponse;
import mx.edu.unpa.ChatEnRed.domains.Conversation;
import mx.edu.unpa.ChatEnRed.domains.Message;
import mx.edu.unpa.ChatEnRed.domains.MessageType;
import mx.edu.unpa.ChatEnRed.domains.User;
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
	

	@Override
	@Transactional(readOnly=true)
	public List<MessageResponse> findAll() {
		// TODO Auto-generated method stub
		return this.messageRepository.findAll().stream()
				.map(messageMapper::toResponse)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly=true)
	public Optional<MessageResponse> findById(Integer id) {
		// TODO Auto-generated method stub
		return this.messageRepository.findById(id)
				.map(messageMapper::toResponse);
	}

//	@Override
//	@Transactional
//	public Optional<MessageResponse> save(MessageRequest request) {
//		// TODO Auto-generated method stub
//		Conversation conversation=this.conversationRepository.findById(request.getConversationId())
//				.orElseThrow(()->new EntityNotFoundException("Conversation not found with id:"+request.getConversationId()));
//		conversation.setLastMessageAt(LocalDateTime.now());
//		this.conversationRepository.save(conversation);
//		User sender =userRepository.findById(request.getSenderId())
//                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getSenderId()));
//
//		MessageType messageType=this.messageTypeRepository.findByCode(request.getMessageTypeCode());
//		Message message=this.messageMapper.toEntity(request, conversation, sender, messageType);
//		return Optional.of(message)
//				.map(messageRepository::save)
//				.map(messageMapper::toResponse);
//	}

	@Override
	@Transactional
	public Optional<Boolean> deleteById(Integer id) {
		// TODO Auto-generated method stub
		return this.messageRepository.findById(id)
				//  Borrado Lógico (Soft Delete)
				// En lugar de borrar la fila, actualizamos deletedAt
				.map(message->{
					message.setDeletedAt(LocalDateTime.now());
					this.messageRepository.save(message);
				return true;
				});
		
	}

//	@Override
//	@Transactional
//	public Optional<MessageResponse> update(Integer id, MessageRequest dto) {
//		// TODO Auto-generated method stub
//		Message existing = messageRepository.findById(id)
//	            .orElseThrow(() -> new EntityNotFoundException("Message not found: " + id));
//
//		Conversation conv = conversationRepository.findById(dto.getConversationId())
//	            .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));
//	    MessageType mt = messageTypeRepository.findByCode(dto.getMessageTypeCode());
//
//	    User sender =userRepository.findById(dto.getSenderId())
//	    		.orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//	    existing.setConversation(conv);
//	    existing.setSender(sender);
//	    existing.setMessageType(mt);
//	    existing.setContent(dto.getContent());
//		existing.setIv(dto.getIv()); // Si cambia el contenido cifrado, cambia el IV
//
//	    return Optional.of(existing)
//	    		.map(messageRepository::save)
//				.map(messageMapper::toResponse);
//
//	}


	@Override
	@Transactional(readOnly = true)
	public List<MessageResponse> getChatMessages(Integer conversationId, String currentUsername) {

		// 1. Obtener usuario actual
		User currentUser = userRepository.findByUsername(currentUsername)
				.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

		// 2. SEGURIDAD: Verificar que el usuario pertenezca al chat
		// Nota: Asegúrate de tener este método en ConversationMemberRepository
		boolean isMember = memberRepository.existsByConversationIdAndUserId(conversationId, currentUser.getId());

		if (!isMember) {
			throw new AccessDeniedException("No tienes permiso para ver los mensajes de este chat.");
		}

		// 3. Obtener mensajes de la BD (Cifrados)
		List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

		// 4. Convertir a DTO y calcular isMine
		return messages.stream().map(msg -> {
			MessageResponse dto = messageMapper.toResponse(msg);
			// Lógica para saber si el mensaje es mío
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

		// 1. Obtener usuario (Remitente)
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
		// Nota: request.setCreatedAt(LocalDateTime.now()) si tu mapper lo requiere explícitamente,
		// pero tu entidad tiene @PrePersist, así que está cubierto.
		Message newMessage = messageMapper.toEntity(request, conversation, sender, msgType);

		// Aseguramos contenido y IV (simulación de cifrado por ahora)
		newMessage.setContent(request.getContent());
		newMessage.setIv(request.getIv());

		// 6. Guardar
		Message savedMessage = messageRepository.save(newMessage);

		// 7. Actualizar "Last Message" en la conversación (Para que suba en la lista de chats)
		conversation.setLastMessageAt(savedMessage.getCreatedAt());
		conversationRepository.save(conversation);

		// 8. Convertir a Respuesta
		MessageResponse response = messageMapper.toResponse(savedMessage);
		response.setMine(true); // Acabamos de enviarlo, así que es mío
		response.setSenderName(sender.getUsername());

		return response;
	}


}
