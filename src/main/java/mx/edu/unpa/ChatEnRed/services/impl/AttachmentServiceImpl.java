package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mx.edu.unpa.ChatEnRed.DTOs.Attachment.Request.AttachmentRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Attachment.Response.AttachmentResponse;
import mx.edu.unpa.ChatEnRed.domains.Attachment;
import mx.edu.unpa.ChatEnRed.domains.Message;
import mx.edu.unpa.ChatEnRed.mappers.AttachmentMapper;
import mx.edu.unpa.ChatEnRed.repositories.AttachmentRepository;
import mx.edu.unpa.ChatEnRed.repositories.MessageRepository;
import mx.edu.unpa.ChatEnRed.services.AttachmentService;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Override
    @Transactional(readOnly=true)
    public List<AttachmentResponse> findAll() {
        return attachmentRepository.findAll().stream()
                .map(attachmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly=true)
    public Optional<AttachmentResponse> findById(Integer id) {
        return this.attachmentRepository.findById(id)
                .map(attachmentMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<AttachmentResponse> save(AttachmentRequest request) {
        Message message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + request.getMessageId()));

        Attachment attachment = this.attachmentMapper.toEntity(request, message);      
        return Optional.of(attachment)
				.map(attachmentRepository::save)
				.map(attachmentMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        return this.attachmentRepository.findById(id)
                .map(attachment -> {attachmentRepository.deleteById(id);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<AttachmentResponse> update(Integer id, AttachmentRequest dto) {
        Attachment existing = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found: " + id));

        Message message = messageRepository.findById(dto.getMessageId())
                .orElseThrow(() -> new EntityNotFoundException("Message not found: " + dto.getMessageId()));

        existing.setMessage(message);
        existing.setFilename(dto.getFilename());
        //existing.setMimeType(dto.getMimeType());
        //existing.setSize(dto.getSize());
        //existing.setStorageUrl(dto.getStorageUrl());
        //existing.setChecksum(dto.getChecksum());
        //existing.setCreatedAt(dto.getCreatedAt());

        return Optional.of(existing)
	    		.map(attachmentRepository::save)
				.map(attachmentMapper::toResponse);
    }
}

