package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mx.edu.unpa.ChatEnRed.DTOs.MessageStatus.Request.MessageStatusRequest;
import mx.edu.unpa.ChatEnRed.DTOs.MessageStatus.Response.MessageStatusResponse;
import mx.edu.unpa.ChatEnRed.domains.Message;
import mx.edu.unpa.ChatEnRed.domains.MessageStatus;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.mappers.MessageStatusMapper;
import mx.edu.unpa.ChatEnRed.repositories.MessageRepository;
import mx.edu.unpa.ChatEnRed.repositories.MessageStatusRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import mx.edu.unpa.ChatEnRed.services.MessageStatusService;

@Service
public class MessageStatusServiceImpl implements MessageStatusService {

    @Autowired
    private MessageStatusRepository messageStatusRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageStatusMapper messageStatusMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MessageStatusResponse> findAll() {
        return messageStatusRepository.findAll()
                .stream()
                .map(messageStatusMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MessageStatusResponse> findById(Integer id) {
        return messageStatusRepository.findById(id)
                .map(messageStatusMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<MessageStatusResponse> save(MessageStatusRequest request) {
        if (request.getMessageId() == null || request.getRecipientId() == null) {
            throw new EntityNotFoundException("messageId and recipientId are required");
        }

        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + request.getMessageId()));

        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new EntityNotFoundException("User (recipient) not found with id: " + request.getRecipientId()));

        MessageStatus messageStatus = messageStatusMapper.toEntity(request, message, recipient);

        return Optional.of(messageStatus)
	    		.map(messageStatusRepository::save)
				.map(messageStatusMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        return messageStatusRepository.findById(id)
                .map(messageStatus -> { messageStatusRepository.deleteById(id);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<MessageStatusResponse> update(Integer id, MessageStatusRequest request) {
        MessageStatus existing = messageStatusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MessageStatus not found: " + id));
        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + request.getMessageId()));

        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new EntityNotFoundException("User (recipient) not found with id: " + request.getRecipientId()));

        existing.setMessage(message);
        existing.setRecipient(recipient);

        return Optional.of(existing)
	    		.map(messageStatusRepository::save)
				.map(messageStatusMapper::toResponse);
    }
}