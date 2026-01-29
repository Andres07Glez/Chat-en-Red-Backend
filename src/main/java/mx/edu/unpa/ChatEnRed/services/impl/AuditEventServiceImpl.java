package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import mx.edu.unpa.ChatEnRed.DTOs.AuditEvent.Request.AuditEventRequest;
import mx.edu.unpa.ChatEnRed.DTOs.AuditEvent.Response.AuditEventResponse;
import mx.edu.unpa.ChatEnRed.domains.AuditEvent;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.mappers.AuditEventMapper;
import mx.edu.unpa.ChatEnRed.repositories.AuditEventRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import mx.edu.unpa.ChatEnRed.services.AuditEventService;

@Service
public class AuditEventServiceImpl implements AuditEventService {

    @Autowired
    private AuditEventRepository auditEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditEventMapper auditEventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AuditEventResponse> findAll() {
        return auditEventRepository.findAll()
                .stream()
                .map(auditEventMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuditEventResponse> findById(Integer id) {
        return auditEventRepository.findById(id)
                .map(auditEventMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<AuditEventResponse> save(AuditEventRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        AuditEvent auditEvent = auditEventMapper.toEntity(request, user);

        if (auditEvent.getCreatedAt() == null) {
            auditEvent.setCreatedAt(LocalDateTime.now());
        }
        return Optional.of(auditEvent)
				.map(auditEventRepository::save)
				.map(auditEventMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        return this.auditEventRepository.findById(id)
                .map(auditEvent -> {auditEventRepository.deleteById(id);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<AuditEventResponse> update(Integer id, AuditEventRequest request) {
        AuditEvent existing = auditEventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AuditEvent not found: " + id));

        if (request.getUserId() == null) {
            throw new EntityNotFoundException("User id is required for AuditEvent");
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        existing.setUser(user);
        existing.setEventType(request.getEventType());
        existing.setEventData(request.getEventData());
        // existing.setCreatedAt(request.getCreatedAt());

        return Optional.of(existing)
	    		.map(auditEventRepository::save)
				.map(auditEventMapper::toResponse);
    }
}