package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import mx.edu.unpa.ChatEnRed.repositories.ConversationRepository;
import mx.edu.unpa.ChatEnRed.repositories.MessageRepository;
import mx.edu.unpa.ChatEnRed.repositories.MessageTypeRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
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

	@Override
	@Transactional
	public Optional<MessageResponse> save(MessageRequest request) {
		// TODO Auto-generated method stub
		Conversation conversation=this.conversationRepository.findById(request.getConversationId())
				.orElseThrow(()->new EntityNotFoundException("Conversation not found with id:"+request.getConversationId()));
		conversation.setLastMessageAt(LocalDateTime.now());
		this.conversationRepository.save(conversation);
		User sender =userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getSenderId()));
		
		MessageType messageType=this.messageTypeRepository.findByCode(request.getMessageTypeCode());
		Message message=this.messageMapper.toEntity(request, conversation, sender, messageType);
		return Optional.of(message)
				.map(messageRepository::save)
				.map(messageMapper::toResponse);
	}

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

	@Override
	@Transactional
	public Optional<MessageResponse> update(Integer id, MessageRequest dto) {
		// TODO Auto-generated method stub
		Message existing = messageRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Message not found: " + id));

		Conversation conv = conversationRepository.findById(dto.getConversationId())
	            .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));
	    MessageType mt = messageTypeRepository.findByCode(dto.getMessageTypeCode());

	    User sender =userRepository.findById(dto.getSenderId())
	    		.orElseThrow(() -> new EntityNotFoundException("User not found"));

	    existing.setConversation(conv);
	    existing.setSender(sender);
	    existing.setMessageType(mt);
	    existing.setContent(dto.getContent());
		existing.setIv(dto.getIv()); // Si cambia el contenido cifrado, cambia el IV

	    return Optional.of(existing)
	    		.map(messageRepository::save)
				.map(messageMapper::toResponse);

	}

}
