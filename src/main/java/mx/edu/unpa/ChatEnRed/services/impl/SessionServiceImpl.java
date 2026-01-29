package mx.edu.unpa.ChatEnRed.services.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import mx.edu.unpa.ChatEnRed.DTOs.Session.Request.SessionRequest;
import mx.edu.unpa.ChatEnRed.DTOs.Session.Response.SessionResponse;
import mx.edu.unpa.ChatEnRed.domains.Session;
import mx.edu.unpa.ChatEnRed.domains.User;
import mx.edu.unpa.ChatEnRed.mappers.SessionMapper;
import mx.edu.unpa.ChatEnRed.repositories.SessionRepository;
import mx.edu.unpa.ChatEnRed.repositories.UserRepository;
import mx.edu.unpa.ChatEnRed.services.SessionService;

@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionMapper sessionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponse> findAll() {
        return sessionRepository.findAll().stream()
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SessionResponse> findById(Integer id) {
        return sessionRepository.findById(id)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional
    public Optional<SessionResponse> save(SessionRequest request) {
        if (request.getUserId() == null) {
            throw new EntityNotFoundException("userId is required for Session");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        Session session = sessionMapper.toEntity(request, user);
        
        return Optional.of(session)
	    		.map(sessionRepository::save)
				.map(sessionMapper::toResponse); 
    }

    @Override
    @Transactional
    public Optional<Boolean> deleteById(Integer id) {
        return sessionRepository.findById(id)
                .map(entity -> {
                    sessionRepository.deleteById(id);
                    return true;
                });
    }

    @Override
    @Transactional
    public Optional<SessionResponse> update(Integer id, SessionRequest request) {
        Session existing = sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + id));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        existing.setUser(user);
        
        return Optional.of(existing)
	    		.map(sessionRepository::save)
				.map(sessionMapper::toResponse);   
        }
}